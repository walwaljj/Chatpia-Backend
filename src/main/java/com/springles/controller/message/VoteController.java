package com.springles.controller.message;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.message.DayDiscussionMessage;
import com.springles.domain.dto.message.DayEliminationMessage;
import com.springles.domain.dto.message.NightVoteMessage;
import com.springles.domain.dto.vote.ConfirmResultResponseDto;
import com.springles.domain.dto.vote.GameSessionVoteRequestDto;
import com.springles.domain.dto.vote.VoteResultResponseDto;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.game.*;
import com.springles.repository.PlayerRedisRepository;
import com.springles.service.GameSessionVoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VoteController {
    private final GameSessionManager gameSessionManager;
    private final GameSessionVoteService gameSessionVoteService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PlayerRedisRepository playerRedisRepository;
    private final MessageManager messageManager;
    private final DayDiscussionManager dayDiscussionManager;
    private final DayEliminationManager dayEliminationManager;

    @MessageMapping("/chat/{roomId}/dayStart")
    private void voteStart (SimpMessageHeaderAccessor accessor, @DestinationVariable Long roomId) {
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        gameSession.changePhase(GamePhase.DAY_VOTE, 100);
        gameSessionManager.passDay(roomId);

        // 종료된 게임인지 체크
        if (!gameSessionManager.existRoomByRoomId(roomId)) {
            throw new CustomException(ErrorCode.GAME_NOT_FOUND);
        }

        log.info("Room {} start Day {} {} ", gameSession.getRoomId(), gameSession.getDay(),
                gameSession.getGamePhase());

        List<Player> players = playerRedisRepository.findByRoomId(gameSession.getRoomId());

        Map<Long, GameRole> alivePlayerMap = new HashMap<>();
        for (Player player : players) {
            log.info("Room {} has Player {} ", gameSession.getRoomId(), player.getMemberName());
            if (player.isAlive()) {
                alivePlayerMap.put(player.getMemberId(), player.getRole());
            }
        }

        gameSessionVoteService.startVote(roomId, gameSession.getPhaseCount(),
                gameSession.getGamePhase(), gameSession.getTimer(), alivePlayerMap);
        int day = gameSession.getDay();

        // 투표 시작 메시지 전송
        messageManager.sendMessage(
                "/sub/chat/" + roomId,
                 day + "번째 날 아침이 밝았습니다. 투표를 시작합니다.",
                roomId, "admin"
        );

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        // 일정 시간(초 단위) 후에 실행하고자 하는 작업을 정의합니다.
        Runnable task = () -> {
            // 실행하고자 하는 코드를 여기에 작성합니다.
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    "마피아로 의심되는 사람을 지목한 뒤 투표해 주십시오.",
                    roomId, "admin"
            );
        };
        // 일정 시간(초 단위)을 지정하여 작업을 예약합니다.
        // 아래의 예제는 5초 후에 작업을 실행합니다.
        executor.schedule(task, 2, TimeUnit.SECONDS);
    }

    @MessageMapping("/chat/{roomId}/vote")
    private void dayVote(SimpMessageHeaderAccessor accessor,
                         @DestinationVariable Long roomId,
                         @Payload GameSessionVoteRequestDto request) {
        String playerName = getMemberName(accessor);
        Long playerId = gameSessionManager.findMemberByMemberName(playerName).getId();
        log.info("Player {} vote {}", playerName, request.getVote());
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);

        Map<Long, Long> voteResult = gameSessionVoteService.vote(roomId, playerId, request);
        Map<Long, Boolean> confirmResult = gameSessionVoteService.confirmVote(roomId, playerId, request);

        if (voteResult == null) {
            throw new CustomException(ErrorCode.FAIL_VOTE);
        }
        else if(confirmResult.size() <= 0) {
            throw new CustomException(ErrorCode.FAIL_CONFIRM_VOTE);
        }
        else {
            Player voted = playerRedisRepository.findById(voteResult.get(playerId)).get();
            String votedPlayerName = voted.getMemberName();
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    votedPlayerName + "가 투표되었습니다.",
                    roomId, "admin"
            );
            int confirmCnt = confirmResult.entrySet().stream()
                    .filter(e -> e.getValue() == true) // confirm == true인 이용자
                    .collect(Collectors.toList()).size();

            int alivePlayerCnt = gameSession.getAliveCivilian()
                    + gameSession.getAliveDoctor()
                    + gameSession.getAlivePolice()
                    + gameSession.getAliveMafia();
            log.info("confirmCnt: {}, alivePlayerCnt: {}", confirmCnt, alivePlayerCnt);
            if (confirmCnt == alivePlayerCnt) { // 살아 있는 모두가 투표를 끝내면 투표 종료
                Map<Long, Long> vote = gameSessionVoteService.endVote(roomId, gameSession.getPhaseCount(), request.getPhase());
                publishMessage(roomId, vote);
            }
        }
    }

    @MessageMapping("/pub/chat/{roomId}/confirm")
    private void confirmVote(SimpMessageHeaderAccessor accessor,
                         @DestinationVariable Long roomId,
                         @Payload GameSessionVoteRequestDto request) {
        String playerName = accessor.getUser().getName();
        Long playerId = gameSessionManager.findMemberByMemberName(playerName).getId();

        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);

        Map<Long, Boolean> confirmResult = gameSessionVoteService.confirmVote(roomId, playerId, request);

        if(confirmResult.size() <= 0) {
            throw new CustomException(ErrorCode.FAIL_CONFIRM_VOTE);
        }
        else {
            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId,
                    ConfirmResultResponseDto.of(confirmResult));

            int confirmCnt = confirmResult.entrySet().stream()
                    .filter(e -> e.getValue() == true) // confirm == true인 이용자
                    .collect(Collectors.toList()).size();

            int alivePlayerCnt = gameSession.getAliveCivilian()
                    + gameSession.getAliveDoctor()
                    + gameSession.getAlivePolice()
                    + gameSession.getAliveMafia();

            if (confirmCnt == alivePlayerCnt) { // 살아 있는 모두가 투표를 끝내면 투표 종료
                gameSessionVoteService.endVote(roomId, gameSession.getPhaseCount(), request.getPhase());
            }
        }
    }

    @MessageMapping("/pub/chat/{roomId}/{roleName}/vote")
    public void nightVote(SimpMessageHeaderAccessor accessor,
                          @DestinationVariable Long roomId,
                          @DestinationVariable GameRole role,
                          @Payload GameSessionVoteRequestDto request) {
        String playerName = accessor.getUser().getName();
        Long playerID = gameSessionManager.findMemberByMemberName(playerName).getId();

        if (request.getPhase() != GamePhase.NIGHT_VOTE) {
            throw new CustomException(ErrorCode.GAME_PHASE_NOT_NIGHT_VOTE);
        }

        // 해당 롤의 투표
        Map<Long, Long> voteResult = gameSessionVoteService.nightVote(roomId, playerID, request, role);
        // 관찰자들을 위한 투표 결과 반환
        Map<Long, Long> forObserver = gameSessionVoteService.getVoteResult(roomId, request);
        if (voteResult != null) {
            // 해당 role에게 전송
            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId + "/" + role, VoteResultResponseDto.of(voteResult));
            // 관찰자에게 전송
            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId + "/" + GameRole.OBSERVER, VoteResultResponseDto.of(voteResult));


        }
    }

    @MessageMapping("/pub/chat/{roomId}/{roleName}/confirm")
    public void confirmNightVote(SimpMessageHeaderAccessor accessor,
                                 @DestinationVariable Long roomId,
                                 @DestinationVariable GameRole role) {
        String playerName = accessor.getUser().getName();
        Long playerId = gameSessionManager.findMemberByMemberName(playerName).getId();

        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        GameSessionVoteRequestDto request = new GameSessionVoteRequestDto();
        request.setPhase(GamePhase.NIGHT_VOTE);

        Map<Long, Boolean> forObserver = gameSessionVoteService.confirmVote(roomId, playerId, request);
        Map<Long, Boolean> confirmResult = gameSessionVoteService.getNightConfirm(roomId, playerId, request, role);

        if(confirmResult.size() <= 0) {
            throw new CustomException(ErrorCode.FAIL_CONFIRM_VOTE);
        }
        else {
            // 해당 role에게 전송
            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId + "/" + role, ConfirmResultResponseDto.of(confirmResult));
            // 관찰자에게 전송
            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId + "/" + GameRole.OBSERVER, ConfirmResultResponseDto.of(forObserver));

            int confirmCnt = forObserver.entrySet().stream()
                    .filter(e -> e.getValue() == true)
                    .collect(Collectors.toList()).size();
            int notCivilainCnt = gameSession.getAlivePolice()
                    + gameSession.getAliveMafia()
                    + gameSession.getAliveDoctor();

            log.info("Room {} Phase {} Confirm {} : Needed {}", roomId, request.getPhase(), confirmCnt, notCivilainCnt);

            // 필요한 인원의 투표가 다 끝나면
            if(confirmCnt == notCivilainCnt) {
                gameSessionVoteService.endVote(roomId, gameSession.getPhaseCount(), request.getPhase());
            }
        }
    }
    public String getMemberName(SimpMessageHeaderAccessor accessor) {
        return accessor.getUser().getName().split(",")[1].split(":")[1].trim();
    }

    private void publishMessage(Long roomId, Map<Long, Long> vote) {
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        log.info("Room {} start Phase {}", roomId, gameSession.getGamePhase());
        if (gameSession.getGamePhase() == GamePhase.DAY_DISCUSSION) {
            DayDiscussionMessage dayDiscussionMessage =
                    new DayDiscussionMessage(roomId, gameSessionVoteService.getSuspiciousList(gameSession, vote));
            dayDiscussionManager.sendMessage(dayDiscussionMessage);
        }
        else if (gameSession.getGamePhase() == GamePhase.DAY_ELIMINATE) {
            DayEliminationMessage dayEliminationMessage =
                    new DayEliminationMessage(roomId, gameSessionVoteService.getEliminationPlayer(gameSession, vote));
            dayEliminationManager.sendMessage(dayEliminationMessage);
        }
    }
}
