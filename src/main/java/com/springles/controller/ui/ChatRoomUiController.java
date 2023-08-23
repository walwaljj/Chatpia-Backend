package com.springles.controller.ui;


import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatting.ChatRoomListResponseDto;
import com.springles.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("v1")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomUiController {

    private final ChatRoomService chatRoomService;


    // 홈으로 가는 controller : addAttribute 로 username 을 전달 해주고 있다.
//    @GetMapping("/detail.html")
//    public String detail(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        if (userDetails != null) {
//            model.addAttribute("username", userDetails.getUsername());  <-- 이 부분
//        }
//        return "detail";


    // 메인 페이지
    @GetMapping("/index")
    public String index() {
        return "home/index";
    }


    // 게임 만들기 페이지 (GET)
    @GetMapping("/add")
    public String writeRoom(Model model, ChatRoomReqDTO requestDto, Long memberId){
        model.addAttribute("requestDto",requestDto);
        return "home/add";
    }
    // 게임 만들기 (POST)
    @PostMapping("/add")
    public String createRoom(@ModelAttribute("requestDto") @Valid ChatRoomReqDTO requestDto){
        chatRoomService.createChatRoom(requestDto);
        return "redirect:index";
    }

    // 채팅방 목록 페이지
    @GetMapping("/list")
    public String chatRoomList(Model model,
                               @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
                               @RequestParam(value = "size", defaultValue = "10", required = false) Integer size){
        Page<ChatRoomListResponseDto> allChatRooms = chatRoomService.findAllChatRooms(page, size);
        model.addAttribute("allChatRooms",allChatRooms);
        return "home/list";
    }
}

