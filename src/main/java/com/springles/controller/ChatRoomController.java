package com.springles.controller;

import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.request.ChatRoomReqDTO;
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
}
