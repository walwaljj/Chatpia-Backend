package com.springles.game;


import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.message.DayDiscussionMessage;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.PlayerRedisRepository;
import com.springles.service.GameSessionVoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayDiscussionManager {
    private final MessageManager messageManager;
    private final GameSessionManager gameSessionManager;
    private final PlayerRedisRepository playerRedisRepository;
    private final GameSessionVoteService gameSessionVoteService;
    private final DayToNightManager dayToNightManager;
    public void sendMessage(DayDiscussionMessage message) {
        log.info("dayDiscussionManager까지 전달 성공");
        Long roomId = message.getRoomId();
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);

        // 죽여야 할 애 하나가 담긴 명단
        List<Long> suspiciousList =
                message.getSuspiciousList();

        log.info("Room {} suspicious List: {}", roomId, suspiciousList.toString());
        if (suspiciousList.isEmpty()) {
            log.info("Room {} suspicious List is Empty", roomId);
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    "동점 투표자가 발생하여 아무도 지목되지 않았습니다.",
                    roomId, "admin"
            );

            // 비어 있다면 그냥 밤으로 바꾸기
            setDayToNight(roomId);
            return;
        }
        else {
            Optional<Player> deadPlayerOptional = playerRedisRepository.findById(suspiciousList.get(0));
            Player deadPlayer = deadPlayerOptional.get();
            log.info("{}가 마피아로 지목되었습니다.", deadPlayer.getNickName());
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    deadPlayer.getNickName() + "님이 마피아로 지목되셨습니다.",
                    roomId, "admin"
            );
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            Runnable task = () -> {
                log.info("최후 변론을 시작합니다.");
                // 실행하고자 하는 코드를 여기에 작성합니다.
                messageManager.sendMessage(
                        "/sub/chat/" + roomId,
                        "60초 동안 최후 변론을 시작합니다.",
                        roomId, "admin"
                );
            };
            executor.schedule(task, 1, TimeUnit.SECONDS);

            List<Player> players = playerRedisRepository.findByRoomId(roomId);

            // 종료된 게임인지 체크
            if (!gameSessionManager.existRoomByRoomId(roomId)) {
                throw new CustomException(ErrorCode.GAME_NOT_FOUND);
            }
            // 현재 게임의 상태 발송
            //simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId, GameStatusRes.of(gameSession, players));

            Map<Long, GameRole> alivePlayerRoles = players.stream()
                    .filter(Player::isAlive)
                    .collect(Collectors.toMap(Player::getMemberId, Player::getRole));

            log.info("Room {} start Day {} {} ", roomId, gameSession.getDay(), gameSession.getGamePhase());
            gameSessionManager.changePhase(roomId, GamePhase.DAY_ELIMINATE);
            gameSession.setGamePhase(GamePhase.DAY_ELIMINATE);
            messageManager.sendMessage(
                    "/sub/chat/" + roomId + "/" + "deadPlayer",
                    deadPlayer);

            gameSessionVoteService.startVote(
                    roomId,
                    gameSession.getPhaseCount(),
                    gameSession.getGamePhase(),
                    gameSession.getTimer(),
                    alivePlayerRoles);
            // 일정 시간(초 단위) 후에 실행하고자 하는 작업을 정의합니다.
            Runnable eliminationTask = () -> {

                // 실행하고자 하는 코드를 여기에 작성합니다.
                log.info("변론 후 최종 투표를 시작합니다.");
                messageManager.sendMessage(
                        "/sub/chat/" + roomId,
                        "변론 후 최종 투표를 시작합니다.",
                        roomId, "admin"
                );
            };

            executor.schedule(eliminationTask, 60, TimeUnit.SECONDS);
        }
    }

    private void setDayToNight(Long roomId) {

        dayToNightManager.sendMessage(roomId);
    }
}

