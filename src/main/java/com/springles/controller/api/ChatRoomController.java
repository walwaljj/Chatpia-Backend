package com.springles.controller.api;

import com.springles.controller.ui.MemberUiController;
import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto;
import com.springles.domain.dto.member.MemberInfoResponse;
import com.springles.domain.dto.response.ResResult;
import com.springles.game.GameSessionManager;
import com.springles.service.ChatRoomService;
import com.springles.service.CookieService;
import com.springles.valid.ValidationSequence;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    private final CookieService cookieService;
    private final GameSessionManager gameSessionManager;

    // 채팅방 생성
    @Operation(summary = "채팅방 생성", description = "채팅방 생성")
    @PostMapping(value = "/chatrooms")
    public ResponseEntity<ResResult> createChatRoom(@Validated({ValidationSequence.class}) @RequestBody ChatRoomReqDTO chatRoomReqDTO, HttpServletRequest request, Authentication auth) {

        String accessToken = cookieService.atkFromCookie(request);
        MemberInfoResponse info = memberUiController.info(accessToken);
        Long id = info.getId();
        String memberName = info.getMemberName();

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
    @GetMapping(value = "/chatroom/{roomId}")
    public ResponseEntity<ResResult> readChatRoom(
            @PathVariable Long roomId
    ) {

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
     * /chatrooms?title={title}
     * /chatrooms?nickname={nickname}
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

    }

    // 채팅방 수정
    @Operation(summary = "채팅방 수정", description = "채팅방 수정")
    @PatchMapping(value = "/chatrooms/{chatroomid}")
    public ResponseEntity<ResResult> updateChatRoom(
            @Valid @RequestBody ChatRoomUpdateReqDto dto,
            @PathVariable Long chatroomid) {

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
            @PathVariable Long chatroomid) {

        chatRoomService.deleteChatRoom(memberId, chatroomid);
        return "redirect:/v1/chatrooms";
    }

    @GetMapping("/chatRooms/{roomId}")
    public ChatRoomResponseDto findRoomInfo(
            @PathVariable Long roomId
    ) {
        return chatRoomService.findChatRoomByChatRoomId(roomId);
    }

    // Player list 조회
    @GetMapping("chatrooms/{room-id}/player-list")
    public ResponseEntity<ResResult> playerList(@PathVariable("room-id") Long roomId) {

        ResponseCode responseCode = ResponseCode.PLAYER_READ;

        return new ResponseEntity<>(ResResult.builder()
                .responseCode(responseCode)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(gameSessionManager.findPlayersByRoomId(roomId))
                .build(), HttpStatus.OK);
    }

    // 채팅방 입장
    @GetMapping("chatrooms/{room-id}/{nick-name}")
    public ResponseEntity<ResResult> enterRoom(@PathVariable("room-id") Long roomId,
                                               @PathVariable("nick-name") String nickName) {

        // 채팅 방 정보
        ResponseCode responseCode = ResponseCode.CHATROOM_ENTER;

        return new ResponseEntity<>(ResResult.builder()
                .responseCode(responseCode)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(chatRoomService.enterChatRoom(roomId, nickName))
                .build(), HttpStatus.OK);
    }

}
