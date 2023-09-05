package com.springles.controller.message;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.vote.ConfirmResultResponseDto;
import com.springles.domain.dto.vote.GameSessionVoteRequestDto;
import com.springles.domain.dto.vote.VoteResultResponseDto;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.game.ChatMessage;
import com.springles.game.GameSessionManager;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VoteController {
    private final GameSessionManager gameSessionManager;
    private final GameSessionVoteService gameSessionVoteService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PlayerRedisRepository playerRedisRepository;

    @MessageMapping("/pub/chat/{roomId}/start")
    private void voteStart (SimpMessageHeaderAccessor accessor,
                            @DestinationVariable Long roomId,
                            @Payload GameSessionVoteRequestDto request) {
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        gameSession.changePhase(GamePhase.DAY_DISCUSSION, 100);
        gameSession.passADay();

        // 종료된 게임인지 체크
        if (!gameSessionManager.existRoomByRoomId(roomId)) {
            throw new CustomException(ErrorCode.GAME_NOT_FOUND);
        }

        log.info("Room {} start Day {} {} ", gameSession.getRoomId(), gameSession.getDay(),
                gameSession.getGamePhase());

        List<Player> players = playerRedisRepository.findByRoomId(gameSession.getRoomId());

        Map<Long, GameRole> alivePlayerMap = new HashMap<>();
        for (Player player : players) {
            if (player.isAlive()) {
                alivePlayerMap.put(player.getMemberId(), player.getRole());
            }
        }

        gameSessionVoteService.startVote(roomId, gameSession.getPhaseCount(),
                gameSession.getGamePhase(), gameSession.getTimer(), alivePlayerMap);
    }

    @MessageMapping("/pub/chat/{roomId}/vote")
    private void dayVote(SimpMessageHeaderAccessor accessor,
                         @DestinationVariable Long roomId,
                         @Payload GameSessionVoteRequestDto request) {
        String playerName = accessor.getUser().getName();
        Long playerId = gameSessionManager.findMemberByMemberName(playerName).getId();

        Map<Long, Long> voteResult = gameSessionVoteService.vote(roomId, playerId, request);
        if (voteResult == null) {
            throw new CustomException(ErrorCode.FAIL_VOTE);
        }
        else {
            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId,
                    VoteResultResponseDto.of(voteResult));
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
}
