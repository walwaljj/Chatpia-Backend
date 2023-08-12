package com.springles.service;

import com.springles.domain.dto.request.ChatRoomReqDTO;
import org.springframework.transaction.annotation.Transactional;

public interface ChatRoomService {
    @Transactional
    Object createChatRoom(ChatRoomReqDTO chatRoomReqDTO);

}
