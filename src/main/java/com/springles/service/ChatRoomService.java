package com.springles.service;

import com.springles.domain.dto.request.ChatRoomReqDTO;
import com.springles.domain.entity.ChatRoom;
import org.springframework.transaction.annotation.Transactional;

public interface ChatRoomService {
    @Transactional
    ChatRoom createChatRoom(ChatRoomReqDTO chatRoomReqDTO);
}
