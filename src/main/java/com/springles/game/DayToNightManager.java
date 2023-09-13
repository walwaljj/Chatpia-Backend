package com.springles.game;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.PlayerRedisRepository;
import com.springles.service.GameSessionVoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayToNightManager {
    private final MessageManager messageManager;
    private final GameSessionManager gameSessionManager;
    private final PlayerRedisRepository playerRedisRepository;
    private final GameSessionVoteService gameSessionVoteService;

    public void sendMessage(Long roomId) {
        log.info("Day To Night Session까지 진행");
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        gameSession.setGamePhase(GamePhase.NIGHT_VOTE);
        gameSessionManager.saveSession(gameSession);

        List<Player> players = playerRedisRepository.findByRoomId(roomId);
        List<Player> voteList = new ArrayList<>();

        for (Player player : players) {
            // 살아 있으면 패스
            if (player.isAlive()) {
                if(player.getRole() != GameRole.OBSERVER && player.getRole() != GameRole.NONE) {
                    voteList.add(player);
                }
                continue;
            }
            // 죽었다면 게임에서 제거하고 관찰자에 추가
            player.setRole(GameRole.OBSERVER);
            playerRedisRepository.save(player);
        }

        log.info("Room {} CIVILIAN: {}, POLICE: {}, MAFIA: {}, DOCTOR: {}",
                roomId,
                gameSession.getAliveCivilian(),
                gameSession.getAlivePolice(),
                gameSession.getAliveMafia(),
                gameSession.getAliveDoctor());

        if (gameSessionManager.isEnd(gameSession)) {
            if (gameSessionManager.mafiaWin(gameSession) == 1) {
                messageManager.sendMessage(
                        "/sub/chat/" + roomId,
                        "마피아팀이 승리하였습니다",
                        gameSession.getRoomId(), "admin"
                );
            }
            else if (gameSessionManager.mafiaWin(gameSession) == 0) {
                messageManager.sendMessage(
                        "/sub/chat/" + roomId,
                        "시민팀이 승리하였습니다",
                        gameSession.getRoomId(), "admin"
                );
            }
            else {
                messageManager.sendMessage(
                        "/sub/chat/" + roomId,
                        "무승부입니다",
                        gameSession.getRoomId(), "admin"
                );
            }

            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    "end",
                    gameSession.getRoomId(), "admin"
            );
            log.info("game end");
            gameSessionManager.endGame(gameSession.getRoomId());
            messageManager.sendMessage(
                    "/sub/chat/" + roomId + "/timer",
                    "end",
                    gameSession.getRoomId(), "admin"
            );
            return;
        }

        // 종료된 게임인지 체크
        if (!gameSessionManager.existRoomByRoomId(roomId)) {
            throw new CustomException(ErrorCode.GAME_NOT_FOUND);
        }

        log.info("Room {} start Day {} {} ", roomId, gameSession.getDay(),
                gameSession.getGamePhase());

        // 시민이 아닌 사람들
        Map<Long, GameRole> aliveNotCivilians = players.stream()
                .filter(Player::isAlive)
                .filter(player -> player.getRole() != GameRole.CIVILIAN)
                .collect(Collectors.toMap(Player::getMemberId, Player::getRole));

        gameSessionVoteService.startVote(roomId, gameSession.getPhaseCount(),
                gameSession.getGamePhase(), gameSession.getTimer(), aliveNotCivilians);

        log.info("NIGHT_VOTE 투표 생성! - {}", roomId);

        messageManager.sendMessage(
                "/sub/chat/" + gameSession.getRoomId(),
                "밤이 되었습니다.",
                gameSession.getRoomId(), "admin"
        );

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable taskOne = () -> {
            messageManager.sendMessage(
                    "/sub/chat/" + gameSession.getRoomId(),
                    "마피아는 죽일 사람을, 의사는 살릴 사람을, 경찰은 조사할 사람을 선택해 주세요.",
                    gameSession.getRoomId(), "admin"
            );
        };

        Runnable taskTwo = () -> {
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    "투표는 30 초입니다.",
                    roomId, "admin"
            );
        };
        executor.schedule(taskOne, 1, TimeUnit.SECONDS);
        executor.schedule(taskTwo, 1, TimeUnit.SECONDS);


        messageManager.sendMessage(
                "/sub/chat/" + roomId + "/voteInfo",
                voteList);

        messageManager.sendMessage(
                "/sub/chat/" + roomId + "/timer",
                "night",
                gameSession.getRoomId(), "admin"
        );
    }
}
