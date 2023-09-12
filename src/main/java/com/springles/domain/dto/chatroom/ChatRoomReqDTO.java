package com.springles.domain.dto.chatroom;


import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import com.springles.valid.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class ChatRoomReqDTO {
    @NotBlank(message = "방 제목은 필수입니다.##", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min = 4, max = 15, message = "방 제목은 4자 이상 15자 이하여야 합니다.##", groups = ValidationGroups.SizeCheckGroup.class)
    @Schema(description = "제목")
    private String title;

    @Max(value = 10, message = "방 인원은 10명 이하이여야 합니다.##", groups = ValidationGroups.SizeCheckGroup.class)
    @Min(value = 5, message = "방 인원은 5명 이상이여야 합니다.##", groups = ValidationGroups.SizeCheckGroup.class)
    @NotNull(message = "방 인원은 필수입니다.##", groups = ValidationGroups.NotEmptyGroup.class)
    @Schema(description = "정원")
    private Long capacity;

    @NotNull(message = "방 상태는 필수입니다.##", groups = ValidationGroups.NotEmptyGroup.class)
    @Schema(description = "상태")
    private Boolean close;

    private String password;

    public static ChatRoom createToEntity(ChatRoomReqDTO chatRoomCreateReqDTO, Long id) {
        return ChatRoom.builder()
                .title(chatRoomCreateReqDTO.getTitle())
                .password(chatRoomCreateReqDTO.getPassword())
                .ownerId(id)
                .state(ChatRoomCode.WAITING)                // 생성시 채팅방 상태는 "대기중"이 기본 값
                .capacity(chatRoomCreateReqDTO.getCapacity())
                .head(1L)                                   // 생성시 참여인원은 방장 1명이 기본값
                .close(chatRoomCreateReqDTO.getClose())
                .build();
    }
}
