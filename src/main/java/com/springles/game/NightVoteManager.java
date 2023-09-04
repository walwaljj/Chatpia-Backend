package com.springles.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.message.NightVoteMessage;
import com.springles.domain.dto.response.GameStatusKillRes;
import com.springles.domain.dto.response.SuspectVoteRes;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NightVoteManager {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameSessionManager gameSessionManager;
    private final PlayerRedisRepository playerRedisRepository;
    private final GameSessionVoteService gameSessionVoteService;
    private final ObjectMapper objectMapper;

    public void sendMessage(String message) throws JsonProcessingException {
        NightVoteMessage nightVoteMessage = objectMapper.readValue(message, NightVoteMessage.class);
        Long roomId = nightVoteMessage.getRoomId();
        Map<GameRole, Long> roleVote = nightVoteMessage.getRoleVoteResult();

        // 직업에 따른 투표 결과
        Long deadPlayerId = roleVote.get(GameRole.MAFIA);
        Long protectedPlayerId = roleVote.get(GameRole.DOCTOR);
        Long suspectPlayerId = roleVote.get(GameRole.POLICE);

        // 기록
        log.info("Room {} NightVote deadPlayer: {}", roomId, deadPlayerId);
        log.info("Room {} NightVote protectedPlayer: {}", roomId, protectedPlayerId);
        log.info("Room {} NightVote suspectPlayer: {}", roomId, suspectPlayerId);

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

        Map<Long, Player> playerMap = players.stream().collect(
                Collectors.toMap(Player::getMemberId, Function.identity()));

        // 살해당한 인간
        Player deadPlayer = playerMap.get(deadPlayerId);

        // 조사한 인간
        Player suspectPlayer = playerMap.get(suspectPlayerId);

        // 목격자들
        List<Long> victims = setNightDay(gameSession, deadPlayerId);

        // 밤이 지나고 이제 낮이 시작
        log.info("Room {} start Day {} {} ",
                gameSession.getRoomId(),
                gameSession.getDay(),
                gameSession.getGamePhase());

        // 밤 투표 결과 모두에게 전송
        simpMessagingTemplate.convertAndSend("/sub/chat/"+roomId, GameStatusKillRes.of(gameSession, players, deadPlayer));

        // 용의자 조사 결과 관찰자와 경찰에게 전송
        if (suspectPlayer == null) {
            throw new CustomException(ErrorCode.SUSPECT_PLAYER_NOT_FOUND);
        } else {
            simpMessagingTemplate.convertAndSend("/sub/chat/"+roomId+"police", SuspectVoteRes.of(suspectPlayer));
            simpMessagingTemplate.convertAndSend("/sub/chat/"+roomId+"observer", SuspectVoteRes.of(suspectPlayer));
        }

    }

    private List<Long> setNightDay(GameSession gameSession, Long deadPlayerId) {
        gameSession.changePhase(GamePhase.NIGHT_TO_DAY, 7);
        List<Long> victims = new ArrayList<>();
        List<Player> players = playerRedisRepository.findByRoomId(gameSession.getRoomId());

        for (Player player : players) {
            // 살아 있으면 패스
            if (player.isAlive()) {
                continue;
            }
            // 죽었다면 게임에서 제거하고 관찰자에 추가
            gameSessionManager.removePlayer(gameSession.getRoomId(), player.getMemberName());
            victims.add(player.getMemberId());
        }

        Optional<Player> deadPlayerOptional = playerRedisRepository.findById(deadPlayerId);
        if (deadPlayerOptional.isPresent()) {
            Player deadPlayer = deadPlayerOptional.get();
            if (deadPlayer.isAlive()) {
                deadPlayer.setAlive(false);
                playerRedisRepository.save(deadPlayer);
                gameSessionManager.removePlayer(gameSession.getRoomId(), deadPlayer.getMemberName());
                victims.add(deadPlayerId);
            }
        }
        else {
            throw new CustomException(ErrorCode.DEAD_PLAYER_NOT_FOUND);
        }
        log.info("Room {} NightVote deadPlayer: {}", gameSession.getRoomId(), deadPlayerId);
        return victims;
    }
}
