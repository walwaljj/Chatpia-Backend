package com.springles.repository;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long>,
    com.springles.repository.custom.ChatRoomJpaRepositoryCustom {
    List<ChatRoomResponseDto> findAllByCloseFalseAndState(ChatRoomCode chatRoomCode);
}
