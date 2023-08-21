package com.springles.repository.custom;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ChatRoomCustomRepository {
    Optional<List<ChatRoom>> findAllByOwnerId(Long ownerId); //방장 이름으로 chatRoom 찾기에서 사용
    Page<ChatRoom> findAllByOpenTrueAndState(ChatRoomCode chatRoomCode, Pageable pageable);

}
