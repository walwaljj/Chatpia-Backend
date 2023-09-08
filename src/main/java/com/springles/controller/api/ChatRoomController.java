package com.springles.controller.api;

import com.springles.controller.ui.MemberUiController;
import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto;
import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.dto.member.MemberInfoResponse;
import com.springles.domain.dto.response.ResResult;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1")
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MemberUiController memberUiController;

    // 채팅방 생성
    @Operation(summary = "채팅방 생성", description = "채팅방 생성")
    @PostMapping(value = "/chatrooms")
    public ResponseEntity<ResResult> createChatRoom(@Valid @RequestBody ChatRoomReqDTO chatRoomReqDTO, HttpServletRequest request, Authentication auth){

        String accessToken = (String) request.getAttribute("accessToken");
        MemberInfoResponse info = memberUiController.info(accessToken);
        Long id = info.getId();
        String memberName = info.getMemberName();

//        log.info("MemberCreateRequest.getMemberName() = {}",((MemberCreateRequest) auth.getPrincipal()).getMemberName()); // 인증 된 멤버 이름
//        log.info("MemberCreateRequest.isAuthenticated() = {}", auth.isAuthenticated()); // 인증 되었다면 true

        // 응답 메시지 return
        ResponseCode responseCode = ResponseCode.CHATROOM_CREATE;
        return new ResponseEntity<>(ResResult.builder()
                .responseCode(responseCode)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(chatRoomService.createChatRoom(chatRoomReqDTO, id, memberName))
                .build(), HttpStatus.OK);
    }

    // 채팅방 단일 조회
    @GetMapping(value="/chatroom/{roomId}")
    public ResponseEntity<ResResult> readChatRoom(
            @PathVariable Long roomId
    ){

        ResponseCode responseCode = ResponseCode.CHATROOM_READ;

        return new ResponseEntity<>(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(chatRoomService.findChatRoomByChatRoomId(roomId))
                        .build(), HttpStatus.OK);
    }

    // 채팅방 전체 조회
    @GetMapping(value = "/chatrooms")
    public ResponseEntity<ResResult> readChatRooms() {

        ResponseCode responseCode = ResponseCode.CHATROOM_READ;

        return new ResponseEntity<>(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(chatRoomService.findAllChatRooms())
                        .build(), HttpStatus.OK);

    }

    /**
     /chatrooms?title={title}
     /chatrooms?nickname={nickname}
     */
    // 채팅방 검색
    @GetMapping(value = "/chatroom/search")
    public ResponseEntity<?> searchChatRooms(
            @RequestParam(value = "content", required = false, defaultValue = "") String content
    ) {
        ResponseCode responseCode = ResponseCode.CHATROOM_SEARCH;
        return new ResponseEntity<>(
            ResResult.builder()
                    .responseCode(responseCode)
                    .code(responseCode.getCode())
                    .message(responseCode.getMessage())
                    .data(chatRoomService.findAllByTitleAndNickname(content))
                    .build(), HttpStatus.OK);

//            // 일치하는 방이 하나도 없을 때
////            model.addAttribute("errorMessage", String.format("'%s'에 해당하는 유저 또는 방을 찾지 못해 전체 목록을 불러옵니다.",searchContent));
//            // 전체 채팅 목록 불러오기
//            List<ChatRoomResponseDto> allChatRooms = chatRoomService.findAllChatRooms();
////            model.addAttribute("allChatRooms", allChatRooms);
//        }
    }

    // 채팅방 수정
    @Operation(summary = "채팅방 수정", description = "채팅방 수정")
    @PatchMapping(value = "/chatrooms/{chatroomid}")
    public ResponseEntity<ResResult> updateChatRoom(
            @Valid @RequestBody ChatRoomUpdateReqDto dto,
            @PathVariable Long chatroomid){

        // 응답 메시지 return
        log.info(String.valueOf(dto.getMemberId()));
        ResponseCode responseCode = ResponseCode.CHATROOM_UPDATE;
        return new ResponseEntity<>(ResResult.builder()
                .responseCode(responseCode)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(chatRoomService.updateChatRoom(dto, chatroomid))
                .build(), HttpStatus.OK);
    }

    // 채팅방 삭제
    @Operation(summary = "채팅방 삭제", description = "채팅방 삭제")
    @DeleteMapping(value = "/chatrooms/{chatroomid}")
    public String deleteChatRoom(
            @RequestBody Long memberId,
            @PathVariable Long chatroomid){

        chatRoomService.deleteChatRoom(memberId, chatroomid);
        return "redirect:/v1/chatrooms";
    }
}
