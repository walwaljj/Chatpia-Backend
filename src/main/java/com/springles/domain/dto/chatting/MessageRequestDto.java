package com.springles.domain.dto.chat;


import com.springles.redis.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class MessageRequestDto {

    private ChatMessage.ChatType type;
    private String message;
    private String sender;
    private String roomId;

    @Override
    public String toString() {
        return "MessageRequestDto{" +
            "type=" + type +
            ", message='" + message + '\'' +
            ", sender='" + sender + '\'' +
            ", roomId=" + roomId +
            '}';
    }
}
