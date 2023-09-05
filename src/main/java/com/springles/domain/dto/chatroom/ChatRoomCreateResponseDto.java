package com.springles.domain.dto.chatroom;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ChatRoomCreateResponseDto {

    private Long id;
    private String title;
    private String password;
    private Long ownerId;
    private ChatRoomCode state;
    private Long capacity;
    private Long head;
    private Boolean close;
    private String nickName;

    public static ChatRoomCreateResponseDto of(ChatRoom chatRoom, String nickName) {
        return ChatRoomCreateResponseDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .password(chatRoom.getPassword())
                .ownerId(chatRoom.getOwnerId())
                .state(chatRoom.getState())
                .capacity(chatRoom.getCapacity())
                .head(chatRoom.getHead())
                .close(chatRoom.getClose())
                .nickName(nickName)
                .build();
    }
}
