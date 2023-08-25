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
    public String writeRoom(Model model, ChatRoomReqDTO requestDto) {
        model.addAttribute("requestDto", requestDto);
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

    // 채팅방 목록 페이지 (전체 조회)
    @GetMapping("/list")
    public String chatRoomList(Model model,
                               @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
                               @RequestParam(value = "size", defaultValue = "5", required = false) Integer size) {
        Page<ChatRoomListResponseDto> allChatRooms = chatRoomService.findAllChatRooms(page, size);
        model.addAttribute("allChatRooms", allChatRooms);
        return "home/list";
    }

    // 채팅방 목록 페이지 (오픈 & 대기 조회)
    @GetMapping("/list/check")
    public String checkedChatRoomList(Model model,
                                      @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
                                      @RequestParam(value = "size", defaultValue = "5", required = false) Integer size) {
        Page<ChatRoomListResponseDto> closeFalseAndStateList = chatRoomService.findAllByCloseFalseAndState(page, size);
        model.addAttribute("allChatRooms", closeFalseAndStateList);
        return "home/check";
    }

    // 채팅방 검색
    @GetMapping("list/search")
    public String searchRooms(@RequestParam(value = "search-content", required = false) String searchContent,
                              @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
                              @RequestParam(value = "size", defaultValue = "5", required = false) Integer size,
                              Model model) {

        try {

            Page<ChatRoomListResponseDto> allByTitleAndNickname = chatRoomService.findAllByTitleAndNickname(searchContent, page, size);
            model.addAttribute("allChatRooms", allByTitleAndNickname);

        } catch (CustomException e) {

            // 검색어가 비어있다면
            if (searchContent.isEmpty()) {

                Page<ChatRoomListResponseDto> allChatRooms = chatRoomService.findAllChatRooms(page, size);
                model.addAttribute("allChatRooms", allChatRooms);
                model.addAttribute("errorMessage", e.getMessage());

            }

            // 검색어가 비어 있지 않고 방을 찾지 못했을 때

            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("search-content",searchContent);
        }

        return "home/list";
    }


}

