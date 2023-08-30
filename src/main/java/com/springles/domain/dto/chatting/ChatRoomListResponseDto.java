package com.springles.domain.dto.chatting;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.dto.member.MemberInfoResponse;
import com.springles.domain.entity.ChatRoom;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.MemberJpaRepository;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ChatRoomListResponseDto {

    private final MemberJpaRepository memberRepository;

    private Long id; // 채팅방 ID
    private String title; // 채팅방 이름
    private Long capacity; // 정원
    private Long head; // 참여자 수
    private ChatRoomCode state; // 채팅방 상태
    private Boolean close; // 공개방 , 비밀방
    private Long ownerId; // 방장 ID
    private String password; // 방 pw


    // Entity -> Dto
    public static ChatRoomListResponseDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomListResponseDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .capacity(chatRoom.getCapacity())
                .head(chatRoom.getHead())
                .state(chatRoom.getState())
                .close(chatRoom.getClose())
                .ownerId(chatRoom.getOwnerId())
                .password(chatRoom.getPassword())
                .build();
    }

    // 방장 ID로 방장 nickname 요청
    public String fromOwnerId(Long ownerId){
        MemberInfoResponse memberInfo = MemberInfoResponse.of(memberRepository.findById(ownerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)));
        return memberInfo.getMemberName();
    }
}
