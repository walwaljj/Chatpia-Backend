package com.springles.domain.dto.chatting;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class MessageRequestDto {

    public enum ChatType {
        ENTER, QUIT, CHAT
    }

    private ChatType type;
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
