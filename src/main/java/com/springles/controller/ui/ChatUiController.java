package com.springles.controller.ui;

import com.google.gson.Gson;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.dto.member.MemberInfoResponse;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.game.GameSessionManager;
import com.springles.jwt.JwtTokenUtils;
import com.springles.service.ChatRoomService;
import com.springles.service.CookieService;
import com.springles.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
@Slf4j
public class ChatUiController {

    @GetMapping("rooms")
    public String rooms() {
        return "rooms";
    }


    @GetMapping
    public String index() {
        return "chat-lobby";
    }


    @GetMapping("enter")
    public String enterRoom(){
        return "chat-room";
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