package com.springles.service;

import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {
    ChatRoomResponseDto createChatRoom(ChatRoomReqDTO chatRoomReqDTO, Long id);
    ChatRoomResponseDto updateChatRoom(ChatRoomUpdateReqDto chatRoomUpdateReqDto, Long chatroomId);
    void deleteChatRoom(Long memberId, Long chatRoomId);

    List<ChatRoomResponseDto> findAllByCloseFalseAndState();
    List<ChatRoomResponseDto> findChatRoomByTitle(String title);
    List<ChatRoomResponseDto> findChatRoomByNickname(String nickname);
    ChatRoomResponseDto findChatRoomByChatRoomId(Long id);
    List<ChatRoomResponseDto> findAllByTitleAndNickname(String searchContent);
    List<ChatRoomResponseDto> findAllChatRooms();
    ChatRoomResponseDto enterChatRoom(Long roomId);
    ChatRoomResponseDto chatRoomCondition(Long roomId);
    ChatRoomResponseDto quickEnter();
}