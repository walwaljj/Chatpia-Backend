package com.springles.controller.ui;


import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("v1/home")
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


}
