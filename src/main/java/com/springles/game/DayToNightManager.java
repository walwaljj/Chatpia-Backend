package com.springles.game;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.message.RoleExplainMessage;
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
import java.util.function.Function;
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
            // gameSessionManager.removePlayer(gameSession.getRoomId(), player.getMemberName());
            playerRedisRepository.save(player);
        }

        // 살아 있는 인원 업데이트
        gameSession.setAliveCivilian((int) players.stream()
                .filter(e -> e.getRole() != GameRole.CIVILIAN)
                .filter(Player::isAlive).count());
        gameSession.setAliveDoctor((int) players.stream()
                .filter(e -> e.getRole() != GameRole.DOCTOR)
                .filter(Player::isAlive).count());
        gameSession.setAliveMafia((int) players.stream()
                .filter(e -> e.getRole() != GameRole.MAFIA)
                .filter(Player::isAlive).count());
        gameSession.setAlivePolice((int) players.stream()
                .filter(e -> e.getRole() != GameRole.POLICE)
                .filter(Player::isAlive).count());

        gameSessionManager.saveSession(gameSession);
        log.info("Room {} CIVILIAN: {}, POLICE: {}, MAFIA: {}, DOCTOR: {}",
                roomId,
                gameSession.getAliveCivilian(),
                gameSession.getAlivePolice(),
                gameSession.getAliveMafia(),
                gameSession.getAliveDoctor());

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
        messageManager.sendMessage(
                "/sub/chat/" + gameSession.getRoomId(),
                "마피아는 죽일 사람을, 의사는 살릴 사람을, 경찰은 조사할 사람을 선택해 주세요.",
                gameSession.getRoomId(), "admin"
        );
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
