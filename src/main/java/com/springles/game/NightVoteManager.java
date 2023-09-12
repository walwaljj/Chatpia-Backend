package com.springles.game;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.message.NightVoteMessage;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.PlayerRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        Map<Long, Player> playerMap = players.stream().collect(
                Collectors.toMap(Player::getMemberId, Function.identity()));

        // 살해당한 인간
        Player deadPlayer = playerMap.get(deadPlayerId);
        log.info("Mafia kill {}", deadPlayer.getMemberName());

        // 목격자들
        List<Long> victims = setNightDay(gameSession, deadPlayerId);

        // 밤이 지나고 이제 낮이 시작
        log.info("Room {} start Day {} {} ",
                gameSession.getRoomId(),
                gameSession.getDay(),
                gameSession.getGamePhase());

        // 밤 투표 결과 모두에게 전송
        if (deadPlayer == null) {
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                     "마피아의 지목을 의사가 살렸습니다.",
                    gameSession.getRoomId(), "admin"
            );
        } else {
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    deadPlayer.getMemberName() + "님이 마피아에게 사망하셨습니다.",
                    gameSession.getRoomId(), "admin"
            );
        }

        // 용의자 조사 결과 관찰자와 경찰에게 전송
        for (Long voter : suspectVote.keySet()) {
            Player suspectPlayer = suspectVote.get(voter);
            log.info("Room {} NightVote suspectPlayer: {}", roomId, suspectPlayer.getMemberId());
            messageManager.sendMessage(
                    "/sub/chat/" + roomId + '/' + GameRole.POLICE,
                    suspectPlayer.getMemberName()+ "님은 " + suspectPlayer.getRole() + "입니다.",
                    gameSession.getRoomId(), "admin"
            );
        }
    }

    private List<Long> setNightDay(GameSession gameSession, Long deadPlayerId) {
        gameSession.changePhase(GamePhase.NIGHT_TO_DAY, 7);
        gameSessionManager.saveSession(gameSession);
        log.info("Room {} is {}", gameSession.getRoomId(), gameSession.getGamePhase());
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

        // 죽을 인간이 존재하면
        if (deadPlayerOptional.isPresent()) {
            Player deadPlayer = deadPlayerOptional.get();
            // 아직 살아 있다면
            if (deadPlayer.isAlive()) {
                log.info("{} 님이 마피아에게 사망하셨습니다.", deadPlayer.getMemberName());
                // 죽임
                deadPlayer.setAlive(false);
                deadPlayer.setRole(GameRole.OBSERVER);
                playerRedisRepository.save(deadPlayer);
                // gameSessionManager.removePlayer(gameSession.getRoomId(), deadPlayer.getMemberName());
                // 관찰자에 추가
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
