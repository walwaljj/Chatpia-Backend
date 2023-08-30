package com.springles.controller.message;

import com.springles.domain.dto.vote.GameSessionVoteRequestDto;
import com.springles.domain.dto.vote.VoteResultResponseDto;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.game.ChatMessage;
import com.springles.game.GameSessionManager;
import com.springles.service.GameSessionVoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VoteController {
    private final GameSessionManager gameSessionManager;
    private final GameSessionVoteService gameSessionVoteService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/pub/chat/{roomId}/vote")
    private void dayVote(SimpMessageHeaderAccessor accessor,
                         @DestinationVariable Long roomId,
                         @Payload GameSessionVoteRequestDto request) {
        String userName = accessor.getUser().getName();
        Long playerId = gameSessionManager.findMemberByMemberName(userName).getId();

        Map<Long, Long> voteResult = gameSessionVoteService.vote(roomId, playerId, request);
        if (voteResult == null) {
            throw new CustomException(ErrorCode.FAIL_VOTE);
        }
        else {
            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId,
                    VoteResultResponseDto.of(voteResult));
        }

    }
}
