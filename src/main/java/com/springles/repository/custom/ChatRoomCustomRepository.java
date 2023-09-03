package com.springles.repository.custom;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomCustomRepository {
    List<ChatRoomResponseDto> findAllByOwnerId(Long ownerId); //방장 이름으로 chatRoom 찾기에서 사용
    List<ChatRoomResponseDto> findAllByCloseFalseAndState(ChatRoomCode chatRoomCode);

}
