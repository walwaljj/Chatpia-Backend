package com.springles.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.entity.ChatRoom;
import com.springles.repository.ChatRoomJpaRepository;
import com.springles.service.ChatRoomService;
import com.springles.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ChatRoomControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ChatRoomJpaRepository chatRoomRepository;
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    MemberService memberService;

    @BeforeEach
    public void init() {

        // head == i
        for (int i = 1; i <= 4; i++) {
            chatRoomRepository.save(
                    new ChatRoom(Long.valueOf(i), "gameRoom" + i, null, Long.valueOf(i), ChatRoomCode.WAITING, 6L, Long.valueOf(i), true) // 오픈 , 대기중
            );
        }

    }

//    @Test 생성순으로 조회되고 있어 수정 예정
    @DisplayName("채팅방 조회 , 빠른 시작이 제일 상단에 위치함")
    public void searchChatRoomsTest() throws Exception {

        // when
        mockMvc.perform(get("/v1/chatrooms")
                        .param("page", "1")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].title").value("gameRoom4"))
                .andDo(print());
    }

    @Test
    @DisplayName("방장 이름으로 조회 ")
    public void searchChatRoomsByOwnerIdTest() throws Exception {

        //given
        for (int i = 1; i <= 2; i++) {
            memberService.signUp(MemberCreateRequest.builder()
                    .memberName("testUser" + i)
                    .password("1")
                    .passwordConfirm("1")
                    .email("1@")
                    .role("user")
                    .isDeleted(false).build());
        }

        //when
        mockMvc.perform(get("/v1/chatrooms")
                        .param("nickname", "uSer1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value("gameRoom1"))
                .andDo(print());
    }

    @Test
    @DisplayName("방 이름으로 조회") // 대소문자 구분함, 필요시 쿼리 dsl 도입 시 적용
    public void searchChatRoomsByTitleTest() throws Exception {

        //when
        mockMvc.perform(get("/v1/chatrooms")
                        .param("title", "Room")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.size()").value(4)) // init메소드에서 title 이 Room인 방을 찾음
                .andDo(print());
    }

    @Test
    @DisplayName("채팅방 생성")
    public void createChatRoomTest() throws Exception {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("채팅방 수정")
    public void updateChatRoomTest() throws Exception {
        // given
        // when
        // then
    }

    @Test
    @DisplayName("채팅방 삭제")
    public void deleteChatRoomTest() throws Exception {
        // given
        // when
        // then
    }
}

