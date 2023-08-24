package com.springles.domain.dto.chatroom;


import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ChatRoomUpdateReqDto {
    private Long id;

    @NotBlank(message = "방 제목은 필수입니다.")
    @Size(min = 4, max = 15, message = "방 제목은 4자 이상 15자 이하여야 합니다.")
    @Schema(description = "제목")
    private String title;

    @Schema(description = "비밀번호")
    private String password;

    @NotNull(message = "member ID는 필수입니다.")
    @Schema(description = "방장 ID")
    private Long memberId;

    private ChatRoomCode state;

    @Max(value = 10, message = "방 인원은 10명 이하이여야 합니다.")
    @Min(value = 5, message = "방 인원은 5명 이상이여야 합니다.")
    @NotNull(message = "방 인원은 필수입니다.")
    @Schema(description = "정원")
    private Long capacity;

    private Long head;

    @NotNull(message = "방 상태는 필수입니다.")
    @Schema(description = "상태")
    private Boolean close;


    // 수정 요청을 받게 되면 ChatRoom을 다시 build()
    public static ChatRoom updateToEntity(ChatRoomUpdateReqDto dto, Long id){
        return ChatRoom.builder()
                .id(id)
                .title(dto.getTitle())
                .password(dto.getPassword())
                .ownerId(dto.getMemberId())
                .state(dto.getState())
                .capacity(dto.getCapacity())
                .head(dto.getHead())
                .close(dto.getClose())
                .build();
    }
}