package com.springles.service;

import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto;
import com.springles.domain.dto.chatting.ChatRoomListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatRoomService {
    @Transactional
    ChatRoomResponseDto createChatRoom(ChatRoomReqDTO chatRoomReqDTO);

    Page<ChatRoomListResponseDto> findAllChatRooms(int pageNumber, int size);

    List<ChatRoomListResponseDto> findChatRoomByTitle(String title);

    List<ChatRoomListResponseDto> findChatRoomByNickname(String nickname);

    @Transactional
    ChatRoomResponseDto updateChatRoom(ChatRoomUpdateReqDto chatRoomUpdateReqDto, Long chatroomid);
    @Transactional
    void deleteChatRoom(Long memberId, Long chatRoomId);
}
