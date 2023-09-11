package com.springles.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.message.DayDiscussionMessage;
import com.springles.domain.dto.message.DayEliminationMessage;
import com.springles.domain.dto.response.GameStatus;
import com.springles.domain.dto.response.GameStatusRes;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.PlayerRedisRepository;
import com.springles.service.GameSessionVoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        DayDiscussionMessage dayDiscussionMessage
                = message;
        log.info("dayDiscussionManager까지 전달 성공");
        Long roomId = dayDiscussionMessage.getRoomId();
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);

        // 죽여야 할 애 하나가 담긴 명단
        List<Long> suspiciousList =
                dayDiscussionMessage.getSuspiciousList();

        log.info("Room {} suspicious List: {}", roomId, suspiciousList.toString());

        Optional<Player> deadPlayerOptional = playerRedisRepository.findById(suspiciousList.get(0));
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
            Player deadPlayer = deadPlayerOptional.get();
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    deadPlayer.getMemberName() + "님이 마피아로 지목되셨습니다.",
                    roomId, "admin"
            );
            ScheduledExecutorService notice = Executors.newSingleThreadScheduledExecutor();
            // 일정 시간(초 단위) 후에 실행하고자 하는 작업을 정의합니다.
            Runnable task = () -> {
                // 실행하고자 하는 코드를 여기에 작성합니다.
                messageManager.sendMessage(
                        "/sub/chat/" + roomId,
                        "60초 동안 최후 변론을 시작합니다.",
                        roomId, "admin"
                );
            };
            // 일정 시간(초 단위)을 지정하여 작업을 예약합니다.
            // 아래의 예제는 5초 후에 작업을 실행합니다.
            notice.schedule(task, 2, TimeUnit.SECONDS);

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
        }
    }


//    private List<Long> setDayElimination(GameSession gameSession, List<Long> suspiciousList) {
//        log.info("suspiciousList: {} in Room {}", suspiciousList.toString(), gameSession.getRoomId());
//
//        List<Long> victims = new ArrayList<>();
//        // 게임 세션 상태 바꾸고 타이머 설정
//        gameSession.changePhase(GamePhase.DAY_ELIMINATE, 30 * suspiciousList.size());
//        // 게임에 참여 중인 플레이어들
//        List<Player> players = playerRedisRepository.findByRoomId(gameSession.getRoomId());
//        for (Player player : players) {
//            if (!player.)
//        }
//        return victims;
//    }

    private void setDayToNight(Long roomId) {
        dayToNightManager.sendMessage(roomId);
    }
}

