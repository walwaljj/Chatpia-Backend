package com.springles.game;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.constants.Role;
import com.springles.domain.entity.ChatRoom;
import com.springles.domain.entity.Member;
import com.springles.repository.ChatRoomJpaRepository;
import com.springles.repository.GameSessionRedisRepository;
import com.springles.repository.MemberJpaRepository;
import com.springles.repository.PlayerRedisRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GameSessionManagerTest {

    @Autowired
    private GameSessionManager gameSessionManager;
    @Autowired
    private GameSessionRedisRepository gameSessionRedisRepository;
    @Autowired
    private PlayerRedisRepository playerRedisRepository;
    @Autowired
    private RoleManager roleManager;
    @Autowired
    ChatRoomJpaRepository chatRoomJpaRepository;
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @BeforeEach
    @Transactional
    void init() {
        // 채팅방, 유저 테스트데이터 생성
        for (long i = 1; i <= 10; i++) {
            chatRoomJpaRepository.save(
                new ChatRoom(i, "testGameRoom" + i, null, i,
                    ChatRoomCode.WAITING, 10L, (long) i, false)
            );
            memberJpaRepository.save(
                Member.builder()
                    .memberName("testName" + i)
                    .password("testPassword" + i)
                    .email("testEmail" + i)
                    .role(Role.USER.toString())
                    .isDeleted(false)
                    .build());
        }
    }

    @AfterEach
    void rollback_Redis() {
        gameSessionRedisRepository.deleteAll();
        playerRedisRepository.deleteAll();
    }

    @Test
    void createGame() {
        //given
        Long testRoomId = 1L;
        ChatRoom chatRoom = chatRoomJpaRepository.findById(testRoomId).get();

        //when
        gameSessionManager.createGame(chatRoom);

        //then
        assertThat(gameSessionRedisRepository.findById(testRoomId).get().getHostId()).isEqualTo(
            chatRoom.getOwnerId());
    }

    @Test
    void addUser() {
        //given
        Long testRoomId = 7L;
        int testPlayerCount = 7;
        ChatRoom chatRoom = chatRoomJpaRepository.findById(testRoomId).get();
        gameSessionManager.createGame(chatRoom);

        //when
        for (long i = 1; i < testPlayerCount; i++) {
            gameSessionManager.addUser(testRoomId, i);
        }

        //then
        for (long i = 1; i < testPlayerCount; i++) {
            assertThat(playerRedisRepository.findById(i)).isNotEmpty();
        }
    }

    @Test
    void removePlayer() {
        //given
        Long testRoomId = 7L;
        int testPlayerCount = 7;
        ChatRoom chatRoom = chatRoomJpaRepository.findById(testRoomId).get();
        gameSessionManager.createGame(chatRoom);
        for (long i = 1; i < testPlayerCount; i++) {
            gameSessionManager.addUser(testRoomId, i);
        }

        //when
        for (long i = 1; i < testPlayerCount; i++) {
            gameSessionManager.removePlayer(testRoomId, i);
        }

        //then
        for (long i = 0; i < testPlayerCount; i++) {
            assertThat(playerRedisRepository.findById(i)).isEmpty();
        }
    }

    @Test
    void startGame() {
        //given
        Long testRoomId = 7L;
        int testPlayerCount = 7;
        ChatRoom chatRoom = chatRoomJpaRepository.findById(testRoomId).get();

        gameSessionManager.createGame(chatRoom);

        for (long i = 1; i < testPlayerCount; i++) {
            gameSessionManager.addUser(testRoomId, i);
        }

        //when
        gameSessionManager.startGame(testRoomId);

        //then
        assertThat(playerRedisRepository.findByRoomId(testRoomId).size()).isEqualTo(
            testPlayerCount);
        assertThat(playerRedisRepository.existsById(chatRoom.getOwnerId())).isEqualTo(true);

    }

    @Test
    void endGame() {
        //given
        Long testRoomId = 7L;
        int testPlayerCount = 7;
        ChatRoom chatRoom = chatRoomJpaRepository.findById(testRoomId).get();
        gameSessionManager.createGame(chatRoom);
        for (long i = 1; i < testPlayerCount; i++) {
            gameSessionManager.addUser(testRoomId, i);
        }
        gameSessionManager.startGame(testRoomId);

        //when
        gameSessionManager.endGame(testRoomId);

        //then
        assertThat(gameSessionRedisRepository.findById(testRoomId).get()
            .getGamePhase()).isEqualTo(GamePhase.READY);
        for (long i = 1; i < testPlayerCount; i++) {
            assertThat(playerRedisRepository.findById(i).get().getRole()).isEqualTo(GameRole.NONE);
        }
    }

    @Test
    void removeGame() {
        //given
        Long testRoomId = 7L;
        int testPlayerCount = 7;
        ChatRoom chatRoom = chatRoomJpaRepository.findById(testRoomId).get();
        gameSessionManager.createGame(chatRoom);
        for (long i = 1; i < testPlayerCount; i++) {
            gameSessionManager.addUser(testRoomId, i);
        }

        for (long i = 1; i < testPlayerCount; i++) {
            gameSessionManager.removePlayer(testRoomId, i);
        }
        gameSessionManager.removePlayer(testRoomId, chatRoom.getOwnerId());

        //when
        gameSessionManager.removeGame(testRoomId);

        //then
        assertThat(gameSessionRedisRepository.findById(testRoomId)).isEqualTo(Optional.empty());
    }
}