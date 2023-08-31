package com.springles.repository;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import com.springles.repository.custom.ChatRoomCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long>, ChatRoomCustomRepository {
    Optional<List<ChatRoom>> findAllByCloseFalseAndState(ChatRoomCode chatRoomCode);
}
