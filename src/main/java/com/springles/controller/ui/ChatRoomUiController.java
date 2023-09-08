package com.springles.controller.ui;


import com.springles.controller.api.MemberController;
import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.dto.member.MemberInfoResponse;
import com.springles.domain.dto.member.MemberProfileResponse;
import com.springles.exception.CustomException;
import com.springles.jwt.JwtTokenUtils;
import com.springles.service.ChatRoomService;
import com.springles.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("v1")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomUiController {

    private final ChatRoomService chatRoomService;
    private final CookieService cookieService;

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
}

