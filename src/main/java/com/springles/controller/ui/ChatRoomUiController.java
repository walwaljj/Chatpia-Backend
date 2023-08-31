package com.springles.controller.ui;


import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatting.ChatRoomListResponseDto;
import com.springles.domain.dto.member.MemberInfoResponse;
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

    // 홈으로 가는 controller : addAttribute 로 username 을 전달 해주고 있다.
//    @GetMapping("/detail.html")
//    public String detail(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        if (userDetails != null) {
//            model.addAttribute("username", userDetails.getUsername());  <-- 이 부분
//        }
//        return "detail";

    // 채팅방 만들기 페이지 (GET)
    @GetMapping("add")
    public String writeRoom(Model model, ChatRoomReqDTO requestDto) {
        model.addAttribute("requestDto", requestDto);
        return "home/add";
    }


    // 채팅방 만들기 (POST)
    @PostMapping("add")
    public String createRoom(@ModelAttribute("requestDto") @Valid ChatRoomReqDTO requestDto, HttpServletRequest request){

        // 쿠키에서 accessToken 가져오기
//        Cookie[] cookies = request.getCookies();
//        String accessToken = cookies[0].getValue();
//        MemberInfoResponse info = memberUiController.info(accessToken);
//        Long id = info.getId();
//        log.info(String.valueOf(id));
//        chatRoomService.createChatRoom(requestDto,id);

        String accessToken = (String) request.getAttribute("accessToken");
        MemberInfoResponse info = memberUiController.info(accessToken);
        Long id = info.getId();
        log.info(String.valueOf(id));
        chatRoomService.createChatRoom(requestDto,id);

        return "redirect:index";
    }

    // 채팅방 목록 페이지 (전체 조회)
    @GetMapping("index")
    public String chatRoomList(Model model,
                               HttpServletRequest request,
                               @RequestParam(value = "search-content", required = false, defaultValue = "") String searchContent
    ) {

        // 목록 전체 조회
        String accessToken = (String)request.getAttribute("accessToken");
        MemberInfoResponse info = memberUiController.info(accessToken);

        model.addAttribute("member",info);

        // 채팅방 검색
        try {
            List<ChatRoomListResponseDto> allByTitleAndNickname = chatRoomService.findAllByTitleAndNickname(searchContent);
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

            List<ChatRoomListResponseDto> allChatRooms = chatRoomService.findAllChatRooms();
            model.addAttribute("allChatRooms", allChatRooms);
        }

        return "home/index";
    }
}

