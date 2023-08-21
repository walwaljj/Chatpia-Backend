package com.springles.domain.dto.chatting;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
    private String time;

    @Override
    public String toString() {
        return "MessageRequestDto{" +
            "type =" + type +
            ", message ='" + message + '\'' +
            ", sender ='" + sender + '\'' +
            ", time ='" + sender + '\'' +
            ", roomId =" + time +
            '}';
    }
}
