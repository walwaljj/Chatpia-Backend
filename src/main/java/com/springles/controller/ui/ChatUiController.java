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
    private final Gson gson;
    private final ChatRoomService chatRoomService;
    private final MemberService memberService;
    private final MemberUiController memberUiController;
    private final GameSessionManager gameSessionManager;
    private final JwtTokenUtils jwtTokenUtils;

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

//    @GetMapping("{roomId}/{userId}")
    public String enterRoom(){
        return "chat-room";
    }


    @GetMapping("{room-id}/{nick-name}")
    public String enterRoom2( @PathVariable("room-id") Long roomId, @PathVariable("nick-name") String nickName,
                            HttpServletRequest request, Model model){

        // 멤버 정보
        String accessToken = jwtTokenUtils.atkFromCookie(request);
        MemberInfoResponse memberInfo = memberUiController.info(accessToken);
        model.addAttribute("member",memberInfo);

        // Player list 조회
        List<Player> players = gameSessionManager.findPlayersByRoomId(roomId);
        model.addAttribute("players",players);
        log.info(players.toString());

        // 채팅 정보
        ChatRoomResponseDto chatRoomInfo = chatRoomService.enterChatRoom(roomId);
        model.addAttribute("chatroom",chatRoomInfo);

        log.info("member name = {}", memberInfo.getMemberName());
        // 입장 시 방 condition 을 확인함.

        try{
            ChatRoomResponseDto chatRoomResponseDto = chatRoomService.chatRoomCondition(roomId);
            model.addAttribute("chatRoomResponseDto",chatRoomResponseDto);

        }catch (CustomException e){
            // 비밀 방 일 경우
            model.addAttribute("errorMessage",e.getMessage());
            if(e.getMessage().equals(ErrorCode.CLOSE_ROOM_ERROR.getMessage())){
                // 알림을 띄우고, 비밀번호를 입력 -> 입력된 비밀번호 == 방 비밀번호 -> 입장
                ChatRoomResponseDto chatRoomResponseDto = chatRoomService.findChatRoomByChatRoomId(roomId);
                model.addAttribute("chatRoomResponseDto",chatRoomResponseDto);

            }

        } catch (Exception e) {
            throw e;
        }
        return "chat-room";
    }

    /**
     * 빠른방 입장
     */
    @GetMapping("quick-enter")
    public String quickEnterRoom( HttpServletRequest request, Model model){

        String accessToken = jwtTokenUtils.atkFromCookie(request);
        MemberInfoResponse memberInfo = memberUiController.info(accessToken);

        model.addAttribute("member",memberInfo);

        // 입장 가능한 방 찾기
        try {
            ChatRoomResponseDto chatRoomResponseDto = chatRoomService.quickEnter();
            model.addAttribute("chatRoomResponseDto", chatRoomResponseDto);
            String nextUrl = String.format("/chat/%s/%s", chatRoomResponseDto.getId(), memberInfo.getMemberName());
            return "redirect:"+ nextUrl;
        }
        // 만약 입장 가능한 방이 없을 때 ? alert 확인 -> 방 만들기 / 취소 : 메인페이지로
        catch (CustomException e){

            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("nextUrl", String.format("/v1/add"));
            return "check/confirm";
        }


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