package com.springles.game;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.entity.ChatRoom;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.repository.GameSessionRedisRepository;
import com.springles.repository.PlayerRedisRepository;
import com.springles.repository.VoteRedisRepository;
import com.springles.repository.VoteRepository;
import com.springles.service.GameSessionVoteService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class GameSessionVoteServiceTest {
    @Autowired
    private GameSessionRedisRepository gameSessionRedisRepository;
    @Autowired
    private PlayerRedisRepository playerRedisRepository;
    @Autowired
    private GameSessionManager gameSessionManager;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private VoteRedisRepository voteRedisRepository;
    @Autowired
    private GameSessionVoteService gameSessionVoteService;

    @BeforeEach
    @Transactional
    void init() {
        // 게임룸 만들기
        ChatRoom chatRoom = new ChatRoom(1L, "testGameRoom" + 1, null, 1L,
                ChatRoomCode.WAITING, 10L, (long) 1, false);
        gameSessionRedisRepository.save(GameSession.of(chatRoom));
        // 참여자 만들기
        for (int i = 0; i < 10; i++) {
            playerRedisRepository.save(Player.of((long) i, 1L, "testPlayer" + i));
        }
        // 스타트 상태 만들기
        gameSessionManager.startGame(1L);
    }

    @AfterEach
    void rollback_Redis() {
        gameSessionRedisRepository.deleteAll();
        playerRedisRepository.deleteAll();
    }

    @Test
    void startVote() {
        // given
        List<Player> playerList = gameSessionManager.findPlayersByRoomId(1L);
        Map<Long, GameRole> players = new HashMap<>();
        for (Player p : playerList) {
            players.put(p.getMemberId(), p.getRole());
        }

        // when
        gameSessionVoteService.startVote(1L, 1, GamePhase.DAY_VOTE, LocalDateTime.now(), players);

        // then
        for (Player p : playerList) {
            assertThat(voteRedisRepository.isExist(p.getMemberId()));
        }
    }
}
