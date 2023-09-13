package com.springles.game;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.message.NightVoteMessage;
import com.springles.domain.dto.message.RoleExplainMessage;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.PlayerRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NightVoteManager {
    private final GameSessionManager gameSessionManager;
    private final PlayerRedisRepository playerRedisRepository;
    private final MessageManager messageManager;

    public void sendMessage(NightVoteMessage nightVoteMessage) {
        log.info("night Vote Manager 까지 전달 성공");
        Long roomId = nightVoteMessage.getRoomId();
        Map<GameRole, Long> roleVote = nightVoteMessage.getRoleVoteResult();
        Map<Long, Player> suspectVote = nightVoteMessage.getSuspectResult();


        // 직업에 따른 투표 결과
        Long deadPlayerId = roleVote.get(GameRole.MAFIA);
        Long protectedPlayerId = roleVote.get(GameRole.DOCTOR);

        // 기록
        log.info("Room {} NightVote deadPlayer: {}", roomId, deadPlayerId);
        log.info("Room {} NightVote protectedPlayer: {}", roomId, protectedPlayerId);

        // 의사가 살렸을 경우 부활
        if (deadPlayerId != null && deadPlayerId == protectedPlayerId) {
            deadPlayerId = null;
        }

        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        List<Player> players = playerRedisRepository.findByRoomId(roomId);

        // 종료된 게임인지 체크
        if (!gameSessionManager.existRoomByRoomId(roomId)) {
            throw new CustomException(ErrorCode.GAME_NOT_FOUND);
        }

        boolean isEnd = setNightDay(gameSession, deadPlayerId);

        if (!isEnd) {
            // 밤이 지나고 이제 낮이 시작
            log.info("Room {} start Day {} {} ",
                    gameSession.getRoomId(),
                    gameSession.getDay(),
                    gameSession.getGamePhase());


            // 용의자 조사 결과 관찰자와 경찰에게 전송
            for (Long voter : suspectVote.keySet()) {
                Optional<Player> policeOptional = playerRedisRepository.findById(voter);
                String policeName = "";
                if (policeOptional.isPresent()) {
                    policeName = policeOptional.get().getMemberName();
                }
                Player suspectPlayer = suspectVote.get(voter);
                log.info("Room {} NightVote suspectPlayer: {}", roomId, suspectPlayer.getMemberId());
                messageManager.sendMessage(
                        "/sub/chat/" + roomId + '/' + GameRole.POLICE + '/' + policeName,
                        suspectPlayer.getMemberName() + "님은 " + suspectPlayer.getRole() + "입니다.",
                        gameSession.getRoomId(), "admin"
                );
            }

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

            Runnable task = () -> {
                messageManager.sendMessage("/sub/chat/" + roomId,
                        "토의를 시작해 주세요. 시간은 60 초입니다.",
                        roomId, "admin");
            };
            executor.schedule(task, 1, TimeUnit.SECONDS);

            messageManager.sendMessage(
                    "/sub/chat/" + roomId + "/timer",
                    "day",
                    gameSession.getRoomId(), "admin"
            );
        }
    }

    private boolean setNightDay(GameSession gameSession, Long deadPlayerId) {
        gameSession.changePhase(GamePhase.NIGHT_TO_DAY, 7);
        Long roomId = gameSession.getRoomId();
        gameSessionManager.saveSession(gameSession);
        log.info("Room {} is {}", gameSession.getRoomId(), gameSession.getGamePhase());
        List<Player> players = playerRedisRepository.findByRoomId(gameSession.getRoomId());
        log.info("deadPlayerId: {}", deadPlayerId);
        for (Player player : players) {
            // 살아 있으면 패스
            if (player.isAlive()) {
                continue;
            }
            // 죽었다면 게임에서 제거하고 관찰자에 추가
            gameSessionManager.removePlayer(gameSession.getRoomId(), player.getMemberName());
        }

        if (deadPlayerId == null) {
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    "아무도 죽지 않았습니다.",
                    gameSession.getRoomId(), "admin"
            );
        }
        else {
            Optional<Player> deadPlayerOptional = playerRedisRepository.findById(deadPlayerId);
            Player deadPlayer = deadPlayerOptional.get();
            // 아직 살아 있다면
            if (deadPlayer.isAlive()) {
                if (deadPlayer.getRole() == GameRole.CIVILIAN) {
                    gameSession.setAliveCivilian(gameSession.getAliveCivilian() - 1);
                } else if (deadPlayer.getRole() == GameRole.MAFIA) {
                    gameSession.setAliveMafia(gameSession.getAliveMafia() - 1);
                } else if (deadPlayer.getRole() == GameRole.DOCTOR) {
                    gameSession.setAliveDoctor(gameSession.getAliveDoctor() - 1);
                } else if (deadPlayer.getRole() == GameRole.POLICE) {
                    gameSession.setAlivePolice(gameSession.getAlivePolice() - 1);
                }
                gameSessionManager.saveSession(gameSession);
                log.info("{} 님이 마피아에게 사망하셨습니다.", deadPlayer.getMemberName());
                messageManager.sendMessage(
                        "/sub/chat/" + roomId,
                        deadPlayer.getMemberName() + "님이 마피아에게 사망하셨습니다.",
                        gameSession.getRoomId(), "admin"
                );
                // 죽임
                deadPlayer.setAlive(false);
                deadPlayer.setRole(GameRole.OBSERVER);
                playerRedisRepository.save(deadPlayer);


                List<Player> playersRe = playerRedisRepository.findByRoomId(gameSession.getRoomId());

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
                    return true;
                }
            }
        }
        log.info("Room {} NightVote deadPlayer: {}", gameSession.getRoomId(), deadPlayerId);
        return false;
    }
}
