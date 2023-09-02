package com.springles.game;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageManager {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessage(String dest,String message, Long roomId, String memberName) {
        convertAndSend(dest+ roomId, message, roomId, memberName);
    }

    public void convertAndSend(String dest, String message, Long roomId, String memberName) {
        simpMessagingTemplate.convertAndSend(dest,
            ChatMessage.builder()
                .message(message)
                .sender(memberName)
                .time(getTimeString())
                .roomId(roomId)
                .build());
    }

    public String getTimeString() {
        return new SimpleDateFormat("HH:mm").format(new Date());
    }
}
