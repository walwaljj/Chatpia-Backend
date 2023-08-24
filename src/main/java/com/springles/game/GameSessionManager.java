package com.springles.game;

import com.springles.domain.constants.GameRole;
import com.springles.domain.entity.ChatRoom;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.repository.GameSessionRedisRepository;
import com.springles.repository.PlayerRedisRepository;
import groovy.util.logging.Slf4j;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GameSessionManager {

    private final GameSessionRedisRepository gameSessionRedisRepository;
    private final PlayerRedisRepository playerRedisRepository;
    private final RoleManager roleManager;

    /* 게임 세션 생성 */
    public void createGame(ChatRoom chatRoom) {
        gameSessionRedisRepository.save(GameSession.of(chatRoom));
    }

    /* 게임 시작 */
    public void startGame(Long roomId) {
        // 플레이어 수 확인
        List<Player> players = playerRedisRepository.findByRoomId(roomId);
        if (players.size() == 0) {
            // 에러코드 필요 (인원 부족)
        }
        roleManager.assignRole(players);
        GameSession gameSession = gameSessionRedisRepository.findById(roomId).orElseThrow(); // 익셉션
        gameSessionRedisRepository.save(gameSession.start(players.size()));
        // 메세지 보내기
    }

    /* 게임 종료 -> 준비 상태로 돌아가기 */
    public void endGame(Long roomId) {
        GameSession gameSession = gameSessionRedisRepository.findById(roomId).orElseThrow();
        gameSession.end();
        gameSessionRedisRepository.save(gameSession);
        List<Player> players = playerRedisRepository.findByRoomId(roomId);
        for (Player player : players) player.updateRole(GameRole.NONE);
    }

    /* 게임 세션 삭제 */
    public void removeGame(Long roomId) {
        List<Player> players = playerRedisRepository.findByRoomId(roomId);
        playerRedisRepository.deleteAll(players);
        gameSessionRedisRepository.deleteById(roomId);
    }

    /* 게임에서 유저 제거 */
    public void removePlayer(Long roomId, Long playerId) {
        Player player = playerRedisRepository.findById(playerId).orElseThrow(); // 에러코드
        GameSession gameSession = gameSessionRedisRepository.findById(roomId).orElseThrow(); // 에러코드
        playerRedisRepository.deleteById(playerId);
        List<Player> players = playerRedisRepository.findByRoomId(roomId);
        if (players.isEmpty()) removeGame(roomId); // 아무도 없다면 방삭제
        else if (Objects.equals(gameSession.getHostId(), playerId)) {
            // 랜덤으로 방장 넘겨주기
            Random random = new Random();
            gameSession.changeHost(players.get(random.nextInt(players.size())).getMemberId());
            gameSessionRedisRepository.save(gameSession);
        }
    }

    /* 게임에 유저 추가 */
    public void addUser(Long roomId, Long memberId) {
        if (playerRedisRepository.countByRoomId(roomId) > 10) {
            // 에러 코드 필요 (정원초과)
        }
        GameSession gameSession = gameSessionRedisRepository.findById(roomId)
            .orElseThrow(); // 에러코드 필요(세션이 존재하지 않음)
        if (playerRedisRepository.existsByMemberId(memberId)) {
            // 에러코드 필요(Player가 이미 존재)
        }
        Player newPlayer = Player.of(memberId, roomId);
        playerRedisRepository.save(newPlayer);
    }

}
