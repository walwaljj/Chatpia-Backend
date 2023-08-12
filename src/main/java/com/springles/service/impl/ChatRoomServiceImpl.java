package com.springles.service.impl;

import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.request.ChatRoomReqDTO;
import com.springles.domain.dto.response.ResResult;
import com.springles.domain.entity.ChatRoom;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.ChatRoomJpaRepository;
import com.springles.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Object createChatRoom(ChatRoomReqDTO chatRoomReqDTO) {
        // 비밀방 선택 - 비밀번호 입력하지 않은 경우라면
        if (!chatRoomReqDTO.getOpen() && chatRoomReqDTO.getPassword()==null) throw new CustomException(ErrorCode.PASSWORD_EMPTY);
        // 제목이 비어있는 경우
        if (chatRoomReqDTO.getTitle() == null) throw new CustomException(ErrorCode.TITLE_EMPTY);
        // 인원이 범위를 벗어나는 경우
        Long capacity = chatRoomReqDTO.getCapacity();
        if (capacity < 5 || capacity > 10) throw new CustomException(ErrorCode.CAPACITY_WRONG);

        // 채팅방 생성하기
        ChatRoom savedChatRoom = chatRoomJpaRepository.save(createToEntity(chatRoomReqDTO));
        // 응답 메시지 return
        ResponseCode responseCode = ResponseCode.CHATROOM_CREATE;
        return new ResponseEntity<>(ResResult.builder()
                .responseCode(responseCode)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(savedChatRoom)
                .build(), HttpStatus.OK);

    }
}
