package com.springles.controller.ui;

import com.google.gson.Gson;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.service.ChatRoomService;
import com.springles.service.MemberService;
import com.springles.websocket.SimpleChatHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatUiController {
    private final SimpleChatHandler simpleChatHandler;
    private final Gson gson;
    private final ChatRoomService chatRoomService;
    private final MemberService memberService;

    @GetMapping("rooms")
    public String rooms() {
        return "rooms";
    }

    @GetMapping("enter")
    public String enter(@RequestParam("username") String username) {
        return "chat";
    }

    @GetMapping
    public String index() {
        return "chat-lobby";
    }

    @GetMapping("{roomId}/{userId}")
    public String enterRoom(){
        return "chat-room";
    }

    // url 공유 하기 버튼
    @GetMapping("{roomId}/detail")
    public String roomDetails(@PathVariable("roomId") Long id , Model model){
        model.addAttribute("chatroom",chatRoomService.findChatRoomByChatRoomId(id));
        return "chat-room-info";
    }

    // 공유하기로 초대된 유저
//    @GetMapping("{roomId}")
//    public String invitationAddress(@PathVariable("roomId") Long id, Authentication auth){
//        if(memberService.memberExists(auth.getName())){
//
//        }
//
//    }
}