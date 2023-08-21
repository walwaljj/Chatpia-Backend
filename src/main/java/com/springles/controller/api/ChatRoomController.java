package com.springles.controller.api;

import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto;
import com.springles.domain.dto.response.ResResult;
import com.springles.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1")
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @Operation(summary = "채팅방 생성", description = "채팅방 생성")
    @PostMapping(value = "/chatrooms", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ResResult> createChatRoom(@Valid @RequestBody ChatRoomReqDTO chatRoomReqDTO){
        // 응답 메시지 return
        ResponseCode responseCode = ResponseCode.CHATROOM_CREATE;
        return new ResponseEntity<>(ResResult.builder()
                .responseCode(responseCode)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(chatRoomService.createChatRoom(chatRoomReqDTO))
                .build(), HttpStatus.OK);
    }

    /**
     /chatrooms?page={page}&size={size}
     /chatrooms?title={title}
     /chatrooms?nickname={nickname}
     */
    @GetMapping(value = "/chatrooms")
    public ResponseEntity<ResResult> searchChatRooms(@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
                                                     @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
                                                     @RequestParam(value = "title", required = false) String title,
                                                     @RequestParam(value = "nickname", required = false) String nickname) {


        Object chatRooms = null;

        // title 로 검색
        if (title != null) { chatRooms = chatRoomService.findChatRoomByTitle(title);}

        // 방장 이름으로 검색
        else if (nickname != null) { chatRooms = chatRoomService.findChatRoomByNickname(nickname); }

        // 대기중인 모든 채팅방
        else { chatRooms = chatRoomService.findAllChatRooms(page, size); }

        ResponseCode responseCode = ResponseCode.CHATROOM_SEARCH;

        return new ResponseEntity<>(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(chatRooms)
                        .build(), HttpStatus.OK);
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
