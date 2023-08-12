package com.springles.controller;

import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.request.ChatRoomReqDTO;
import com.springles.domain.dto.response.ResResult;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @PostMapping(value = "/chatrooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResResult> createChatRoom(@Valid @RequestBody ChatRoomReqDTO chatRoomReqDTO){
        // request 자체가 빈 경우
        if (chatRoomReqDTO == null) return ResponseEntity.badRequest().build();
        // 비밀방 선택- 비밀번호 입력하지 않은 경우라면
        if (!chatRoomReqDTO.getOpen() && chatRoomReqDTO.getPassword() == null) throw new CustomException(ErrorCode.PASSWORD_EMPTY);
        // 제목이 비어있는 경우
        if (chatRoomReqDTO.getTitle() == null) throw new CustomException(ErrorCode.TITLE_EMPTY);
        // 인원이 범위를 벗어나는 경우
        Long capacity = chatRoomReqDTO.getCapacity();
        if (capacity < 5 || capacity > 10) throw new CustomException(ErrorCode.CAPACITY_WRONG);
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
