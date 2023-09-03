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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayDiscussionManager {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameSessionManager gameSessionManager;
    private final PlayerRedisRepository playerRedisRepository;
    private final GameSessionVoteService gameSessionVoteService;
    private final ObjectMapper objectMapper;
    public void sendMessage(String message) throws JsonProcessingException {
        DayDiscussionMessage dayDiscussionMessage
                = objectMapper.readValue(message, DayDiscussionMessage.class);

        Long roomId = dayDiscussionMessage.getRoomId();
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);

        // 죽여야 할 애 하나가 담긴 명단
        List<Long> suspiciousList =
                dayDiscussionMessage.getSuspiciousList();

        // DAY_TO_NIGHT
        if(suspiciousList.isEmpty()) {
            // 비어 있다면 그냥 밤으로 바꾸기
            setDayToNight(roomId);
            return;
        }

        // DAY ELIMINATION
        // TODO 조금만 쉬고 사람 죽이기

        List<Player> players = playerRedisRepository.findByRoomId(roomId);

        // 종료된 게임인지 체크
        if (!gameSessionManager.existRoomByRoomId(roomId)) {
            throw new CustomException(ErrorCode.GAME_NOT_FOUND);
        }


        // 현재 게임의 상태 발송
        simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId, GameStatusRes.of(gameSession, players));

        Map<Long, GameRole> alivePlayerRoles = players.stream()
                .filter(Player::isAlive)
                .collect(Collectors.toMap(Player::getMemberId, Player::getRole));

        log.info("Room {} start Day {} {} ", roomId, gameSession.getDay(), gameSession.getGamePhase());

        gameSessionVoteService.startVote(
                roomId,
                gameSession.getPhaseCount(),
                gameSession.getGamePhase(),
                gameSession.getTimer(),
                alivePlayerRoles);

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
        log.info("no suspiciousList at {}", roomId);
        DayEliminationMessage dayEliminationMessage = new DayEliminationMessage();

    }
}

