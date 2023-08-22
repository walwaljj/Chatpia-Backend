package com.springles.domain.dto.chatroom;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ChatRoomResponseDto {

    private Long id;
    private String title;
    private String password;
    private Long ownerId;
    private ChatRoomCode state;
    private Long capacity;
    private Long head;
    private boolean close;

    public static ChatRoomResponseDto of(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
            .id(chatRoom.getId())
            .title(chatRoom.getTitle())
            .password(chatRoom.getPassword())
            .ownerId(chatRoom.getOwnerId())
            .state(chatRoom.getState())
            .capacity(chatRoom.getCapacity())
            .head(chatRoom.getHead())
            .close(chatRoom.getClose())
            .build();
    }
}
