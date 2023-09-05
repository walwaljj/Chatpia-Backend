package com.springles.controller.ui;


import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.dto.member.MemberInfoResponse;
import com.springles.domain.dto.member.MemberProfileResponse;
import com.springles.exception.CustomException;
import com.springles.service.ChatRoomService;
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

    private final MemberUiController memberUiController;
    private final ChatRoomService chatRoomService;

    // 채팅방 만들기 페이지 (GET)
    @GetMapping("add")
    public String chatroom() {
        return "home/add";
    }

    // 채팅방 목록 페이지 (전체 조회)
    @GetMapping("index")
    public String chatRoomList(Model model,
                               HttpServletRequest request,
                               @RequestParam(value = "search-content", required = false, defaultValue = "") String searchContent
    ) {
        // 목록 전체 조회
        String accessToken = (String)request.getAttribute("accessToken");
        // 회원 정보 호출
//        MemberInfoResponse info = memberUiController.info(accessToken);
        // 회원 프로필 정보 호출
        MemberProfileResponse profileInfo = memberUiController.profileInfo(accessToken);
        model.addAttribute("member",profileInfo);
        // 채팅방 검색
        try {
            List<ChatRoomResponseDto> allByTitleAndNickname = chatRoomService.findAllByTitleAndNickname(searchContent);
            model.addAttribute("allChatRooms", allByTitleAndNickname);

        } catch (CustomException e) {

            // 검색어가 비어있다면
            if (searchContent.isBlank()) {
                model.addAttribute("errorMessage", e.getMessage());
            }

            // 검색어가 비어 있지 않고 방을 찾지 못했을 때
            else{
                model.addAttribute("errorMessage", String.format("'%s'에 해당하는 유저 또는 방을 찾지 못해 전체 목록을 불러옵니다.",searchContent) );
            }

            List<ChatRoomResponseDto> allChatRooms = chatRoomService.findAllChatRooms();
            model.addAttribute("allChatRooms", allChatRooms);
        }

        return "home/index";
    }
}

