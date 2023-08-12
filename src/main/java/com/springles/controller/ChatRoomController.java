package com.springles.controller;

import com.springles.domain.dto.request.ChatRoomReqDTO;
import com.springles.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1")
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @Operation(summary = "채팅방 생성", description = "채팅방 생성")
    @PostMapping(value = "/chatrooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createChatRoom(@Valid @RequestBody ChatRoomReqDTO chatRoomReqDTO){
            return chatRoomService.createChatRoom(chatRoomReqDTO);
    }
}
