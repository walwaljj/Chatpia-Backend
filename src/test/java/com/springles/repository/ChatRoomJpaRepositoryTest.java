
package com.springles.repository;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.entity.ChatRoom;
import com.springles.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChatRoomJpaRepositoryTest {

    @Autowired
    ChatRoomJpaRepository chatRoomRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @BeforeEach
    public void init() {

        for (int i = 1; i <= 2; i++) {
            chatRoomRepository.save(
                    new ChatRoom(Long.valueOf(i), "gameRoom" + i, null, Long.valueOf(i), ChatRoomCode.WAITING, 6L, Long.valueOf(i), true) // 오픈방, 대기중
            );
        }

    }


    @Test
    @DisplayName("전체 조회 테스트 ( 오픈된 방 이면서 대기 중인 방만 조회)")
    public void findAllByOpenTrueAndStateTest() {

        // given
        chatRoomRepository.save(new ChatRoom(3L, "gameRoom3", "1111", 3L, ChatRoomCode.WAITING, 8L, 7L, false));// 비밀방, 대기중
        chatRoomRepository.save(new ChatRoom(4L, "gameRoom4", null, 3L, ChatRoomCode.PLAYING, 8L, 8L, true));// 오픈방, 게임중

        // when
        Optional<List<ChatRoom>> allByOpenTrueAndState = chatRoomRepository.findAllByOpenTrueAndState(ChatRoomCode.WAITING);

        // then
        assertThat(allByOpenTrueAndState.get().size()).isEqualTo(2); // 오픈되지 않은 방, 게임중인 방은 조회 되지 않음.
    }

    @Test
    @DisplayName("방장 아이디로 조회")
    public void findAllByOwnerIdTest() {

        // given
        for (int i = 1; i <= 2; i++) {
            memberService.signUp(MemberCreateRequest.builder()
                    .memberName("testUser" + i)
                    .password("1")
                    .passwordConfirm("1")
                    .email("1@")
                    .role("user")
                    .isDeleted(false).build());
        }

        // when
        Optional<ChatRoom> allByOwnerId = chatRoomRepository.findAllByOwnerId(1L);

        //then
        assertThat(allByOwnerId.get().getTitle()).isEqualTo("gameRoom1");
    }

}
