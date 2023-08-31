package com.springles.repository.custom;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomCustomRepository {
    Optional<List<ChatRoom>> findAllByOwnerId(Long ownerId); //방장 이름으로 chatRoom 찾기에서 사용
    Optional<List<ChatRoom>> findAllByCloseFalseAndState(ChatRoomCode chatRoomCode);

}
