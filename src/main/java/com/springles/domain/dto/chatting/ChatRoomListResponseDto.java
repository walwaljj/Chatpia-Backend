package com.springles.domain.dto.chatting;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListResponseDto {

    private String title; // 채팅방 이름
    private Long capacity; // 정원
    private Long head; // 참여자 수
    private ChatRoomCode state; // 채팅방 상태

    public static ChatRoomListResponseDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomListResponseDto.builder()
                .title(chatRoom.getTitle())
                .capacity(chatRoom.getCapacity())
                .head(chatRoom.getHead())
                .state(chatRoom.getState())
                .build();
    }
}
