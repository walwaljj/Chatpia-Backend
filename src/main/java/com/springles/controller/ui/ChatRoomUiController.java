package com.springles.controller.ui;


import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatting.ChatRoomListResponseDto;
import com.springles.domain.dto.member.MemberInfoResponse;
import com.springles.service.ChatRoomService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

    private final MemberUiController memberUiController;
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


    // 채팅방 만들기 페이지 (GET)
    @GetMapping("/add")
    public String writeRoom(Model model, ChatRoomReqDTO requestDto){
        model.addAttribute("requestDto",requestDto);
        return "home/add";
    }

    // 채팅방 만들기 (POST)
    @PostMapping("/add")
    public String createRoom(@ModelAttribute("requestDto") @Valid ChatRoomReqDTO requestDto, HttpServletRequest request){
        // 쿠키에서 accessToken 가져오기
        Cookie[] cookies = request.getCookies();
        String accessToken = cookies[0].getValue();
        // 사용자 정보 가져오는 api 호출
        MemberInfoResponse info = memberUiController.info(accessToken);
        Long id = info.getId();
        log.info(String.valueOf(id));
        chatRoomService.createChatRoom(requestDto, id);
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

    // 채팅방 검색
    @GetMapping("list/search")
    public String searchRooms( @RequestParam(value = "searchContent", required = false) String searchContent,
                               @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
                               Model model) {

        Page<ChatRoomListResponseDto> allByTitleAndNickname = chatRoomService.findAllByTitleAndNickname(searchContent, page);
        model.addAttribute("allChatRooms", allByTitleAndNickname);
        return "home/list";
    }


}

