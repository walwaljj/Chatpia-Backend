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

    List<ChatRoomListResponseDto> findAllByCloseFalseAndState();
    List<ChatRoomListResponseDto> findChatRoomByTitle(String title);
    List<ChatRoomListResponseDto> findChatRoomByNickname(String nickname);
    ChatRoomResponseDto findChatRoomByChatRoomId(Long id);
    List<ChatRoomListResponseDto> findAllByTitleAndNickname(String searchContent);
    List<ChatRoomListResponseDto> findAllChatRooms();
    ChatRoomResponseDto enterChatRoom(Long roomId);
    ChatRoomResponseDto chatRoomCondition(Long roomId);
    ChatRoomResponseDto quickEnter();
}