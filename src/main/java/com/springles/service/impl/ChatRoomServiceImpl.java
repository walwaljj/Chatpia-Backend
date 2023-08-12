package com.springles.service.impl;

import com.springles.domain.dto.request.ChatRoomReqDTO;
import com.springles.domain.entity.ChatRoom;
import com.springles.repository.ChatRoomJpaRepository;
import com.springles.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.springles.domain.dto.request.ChatRoomReqDTO.createToEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @Transactional
    @Override
    public ChatRoom createChatRoom(ChatRoomReqDTO chatRoomReqDTO) {
        // 채팅방 생성하기
        return chatRoomJpaRepository.save(createToEntity(chatRoomReqDTO));
    }
}
