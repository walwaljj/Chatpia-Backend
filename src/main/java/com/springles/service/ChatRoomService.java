package com.springles.service;

import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto;

import com.springles.domain.entity.ChatRoom;
import java.util.List;

public interface ChatRoomService {
    ChatRoom createChatRoom(ChatRoomReqDTO chatRoomReqDTO, Long id);
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