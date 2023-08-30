package com.springles.service;

import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto;
import com.springles.domain.dto.chatting.ChatRoomListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {
    ChatRoomResponseDto createChatRoom(ChatRoomReqDTO chatRoomReqDTO, Long id);
    ChatRoomResponseDto updateChatRoom(ChatRoomUpdateReqDto chatRoomUpdateReqDto, Long chatroomId);
    void deleteChatRoom(Long memberId, Long chatRoomId);

    Page<ChatRoomListResponseDto> findAllByCloseFalseAndState(Integer pageNumber, Integer size);
    List<ChatRoomListResponseDto> findChatRoomByTitle(String title);
    List<ChatRoomListResponseDto> findChatRoomByNickname(String nickname);
    ChatRoomResponseDto findChatRoomByChatRoomId(Long id);
    Page<ChatRoomListResponseDto> findAllByTitleAndNickname(String searchContent, Integer page,Integer size);
    Page<ChatRoomListResponseDto> findAllChatRooms(Integer pageNumber, Integer size);
    ChatRoomResponseDto chatRoomCondition(Long roomId);
    ChatRoomResponseDto quickEnter();
}