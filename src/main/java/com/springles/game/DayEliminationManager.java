package com.springles.game;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.message.DayEliminationMessage;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.PlayerRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayEliminationManager {
    private final GameSessionManager gameSessionManager;
    private final PlayerRedisRepository playerRedisRepository;
    private final MessageManager messageManager;
    private final DayToNightManager dayToNightManager;


    public void sendMessage(DayEliminationMessage dayEliminationMessage) {
        log.info("Day Elimination 단계까지 왔다");
        Long roomId = dayEliminationMessage.getRoomId();
        Long deadPlayerId = dayEliminationMessage.getDeadPlayerId();

        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        log.info("Room: {}, Dead Player Id: {}", roomId, deadPlayerId);
        log.info("Room {} at Phase {}", roomId, gameSession.getGamePhase());

        setDayToNight(gameSession, deadPlayerId);

        List<Player> players = playerRedisRepository.findByRoomId(roomId);
        // 종료된 게임인지 체크
        if (!gameSessionManager.existRoomByRoomId(roomId)) {
            throw new CustomException(ErrorCode.GAME_NOT_FOUND);
        }

        // 죽은 사람이 존재하는 플레이어긴 했는지 검사
        Optional<Player> deadPlayerOptional = playerRedisRepository.findById(deadPlayerId);
        if (deadPlayerOptional.isEmpty()) {
            throw new CustomException(ErrorCode.PLAYER_NOT_FOUND);
        }
        // 현재 진행 상황 기록
        log.info("Room {} start Day {} {} ", roomId, gameSession.getDay(), gameSession.getGamePhase());

        dayToNightManager.sendMessage(roomId);
    }

    private void setDayToNight(GameSession gameSession, Long deadPlayerId)    {
        gameSessionManager.changePhase(gameSession.getRoomId(), GamePhase.DAY_TO_NIGHT);
        // 죽어서 관찰만 하는 사람들
        List<Player> players = playerRedisRepository.findByRoomId(gameSession.getRoomId());

        for (Player player : players) {
            // 살아 있으면 패스
            if (player.isAlive()) {
                continue;
            }
            // 죽었다면 게임에서 제거하고 관찰자에 추가
            player.setRole(GameRole.OBSERVER);
            playerRedisRepository.save(player);
        }

        // 죽을 인간
        Optional<Player> deadPlayerOptional = playerRedisRepository.findById(deadPlayerId);
        // 죽을 인간이 존재하면
        if (deadPlayerOptional.isPresent()) {
            Player deadPlayer = deadPlayerOptional.get();
            // 아직 살아 있다면
            if (deadPlayer.isAlive()) {
                log.info("{} 님이 마피아로 지목되어 사망하셨습니다.", deadPlayer.getMemberName());
                // 죽인 결과 전송
                messageManager.sendMessage(
                        "/sub/chat/" + gameSession.getRoomId(),
                        deadPlayer.getMemberName() + "님이 마피아로 지목되어 사망하셨습니다.",
                        gameSession.getRoomId(), "admin"
                );
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                Runnable task = () -> {
                    messageManager.sendMessage(
                            "/sub/chat/" + gameSession.getRoomId(),
                            deadPlayer.getMemberName() + "님은 " + deadPlayer.getRole() + "입니다.",
                            gameSession.getRoomId(), "admin"
                    );
                };
                executor.schedule(task, 1, TimeUnit.SECONDS);

                // 죽임
                deadPlayer.setAlive(false);
                deadPlayer.setRole(GameRole.OBSERVER);
                playerRedisRepository.save(deadPlayer);

                if (gameSessionManager.isEnd(gameSession)) {
                    gameSessionManager.endGame(gameSession.getRoomId());
                }

            }
        }
        log.info("Room {} ElimainationVote deadPlayer: {}", gameSession.getRoomId(), deadPlayerId);
    }
}
