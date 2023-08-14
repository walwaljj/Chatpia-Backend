package com.springles.service.impl;


import com.springles.domain.dto.request.ChatRoomReqDTO;

import com.springles.domain.entity.ChatRoom;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
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
        // request 자체가 빈 경우
        if (chatRoomReqDTO == null) throw new CustomException(ErrorCode.REQUEST_EMPTY);
        // 비밀방 선택 - 비밀번호 입력하지 않은 경우라면
        if (!chatRoomReqDTO.getOpen() && chatRoomReqDTO.getPassword() == null) throw new CustomException(ErrorCode.PASSWORD_EMPTY);
        // 공개방 선택 - 비밀번호 입력한 경우라면
        if (chatRoomReqDTO.getOpen() && chatRoomReqDTO.getPassword() != null) throw new CustomException(ErrorCode.OPEN_PASSWORD);
        // 방 제목이 비어있는 경우
        if (chatRoomReqDTO.getTitle() == null) throw new CustomException(ErrorCode.TITLE_EMPTY);
        // 인원이 범위를 벗어나는 경우
        Long capacity = chatRoomReqDTO.getCapacity();
        if (capacity < 5 || capacity > 10) throw new CustomException(ErrorCode.CAPACITY_WRONG);

        // 채팅방 생성하기
        return chatRoomJpaRepository.save(createToEntity(chatRoomReqDTO));
    }
}
