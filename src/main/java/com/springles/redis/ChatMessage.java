package com.springles.redis;

import com.springles.domain.dto.chat.MessageRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
public class ChatMessage {

    public enum ChatType {
        ENTER, QUIT, CHAT
    }

    private ChatType type;
    private String message;
    private String sender;
    private String roomId;

    public static ChatMessage of(MessageRequestDto requestDto) {
        return ChatMessage.builder()
            .message(requestDto.getMessage())
            .roomId(requestDto.getRoomId())
            .sender(requestDto.getSender())
            .type(requestDto.getType())
            .build();
    }

    public void enterMessage() {
        this.message = this.sender + "님이 입장하셨습니다.";
    }

    public void quitMessage() {
        this.message = this.sender + "님이 퇴장하셨습니다.";
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
            "type=" + type +
            ", message='" + message + '\'' +
            ", sender='" + sender + '\'' +
            ", roomId=" + roomId +
            '}';
    }
}
