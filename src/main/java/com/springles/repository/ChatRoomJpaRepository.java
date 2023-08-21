package com.springles.repository;

import com.springles.domain.entity.ChatRoom;
import com.springles.repository.custom.ChatRoomCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long>, ChatRoomCustomRepository {

}
