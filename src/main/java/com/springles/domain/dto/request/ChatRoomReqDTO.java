package com.springles.domain.dto.request;


import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ChatRoomReqDTO {
    @NotNull
    private Long memberId;

    @NotBlank(message = "방 제목은 필수입니다.")
    @Size(max = 15, message = "15글자가 넘으면 안됩니다.")
    private String title;

    private Long capacity;

    private Boolean open;

    private String password;

    public static ChatRoom createToEntity(ChatRoomReqDTO chatRoomCreateReqDTO){
        return ChatRoom.builder()
                .title(chatRoomCreateReqDTO.getTitle())
                .password(chatRoomCreateReqDTO.getPassword())
                .ownerId(chatRoomCreateReqDTO.getMemberId())
                .state(ChatRoomCode.WAITING)
                .capacity(chatRoomCreateReqDTO.getCapacity())
                .head(1L)
                .open(chatRoomCreateReqDTO.getOpen())
                .build();
    }
}
