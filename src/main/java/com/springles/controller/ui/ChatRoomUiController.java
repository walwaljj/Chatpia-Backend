package com.springles.controller.ui;

import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("v1")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomUiController {

    private final ChatRoomService chatRoomService;

    // 채팅방 만들기 페이지 (GET)
    @GetMapping("add")
    public String chatroom() {
        return "home/add";
    }

    // 채팅방 목록 페이지 (전체 조회)
    @GetMapping("index")
    public String chatRoomList() {
        return "home/index";
    }

    // 채팅방 입장
    @GetMapping("chat/{room-id}/{nick-name}")
    public String enterRoom(@PathVariable("room-id") Long roomId,
                            @PathVariable("nick-name") String nickName){
        return "chat-room";
    }

}

