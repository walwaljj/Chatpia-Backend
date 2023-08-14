package com.springles.controller;

import com.springles.domain.dto.Chat.Message;
import com.springles.domain.dto.Chat.Message.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/message")
    public void message(Message message) {
        if (message.getType().equals(MessageType.ENTER)) {
            message.setMessage(message.getSenderName() + "님이 입장하셨습니다.");
        }
        if (message.getType().equals(MessageType.EXIT)) {
            message.setMessage(message.getSenderName() + "님이 퇴장하셨습니다.");
        }
        messagingTemplate.convertAndSend("/sub/room/" + message.getRoomId(), message);
    }
}
