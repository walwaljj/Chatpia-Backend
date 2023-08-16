package com.springles.repository;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findAllByOwnerId(Long id); //방장 이름으로 chatRoom 찾기

    Optional<List<ChatRoom>> findAllByOpenTrueAndState(ChatRoomCode state); //공개방 이면서 state
}
