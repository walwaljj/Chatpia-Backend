package com.springles.controller.api;

import com.springles.domain.dto.chat.MessageRequestDto;
import com.springles.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatMessageController {

    private final ChatService chatService;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(MessageRequestDto requestDto) {
        log.info(requestDto.toString());
        chatService.messageResolver(requestDto);
    }
}