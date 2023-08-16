package com.springles.service;

import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.entity.ChatRoom;
import org.springframework.transaction.annotation.Transactional;

public interface ChatRoomService {
    @Transactional
    ChatRoomResponseDto createChatRoom(ChatRoomReqDTO chatRoomReqDTO);
}
