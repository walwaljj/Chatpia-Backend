package com.springles.service.impl;


import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto;
import com.springles.domain.dto.chatting.ChatRoomListResponseDto;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.entity.ChatRoom;
import com.springles.domain.entity.Member;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.ChatRoomJpaRepository;
import com.springles.repository.MemberJpaRepository;
import com.springles.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.springles.domain.dto.chatroom.ChatRoomReqDTO.createToEntity;
import static com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto.updateToEntity;
import static com.springles.exception.constants.ErrorCode.CLOSE_ROOM_ERROR;
import static com.springles.exception.constants.ErrorCode.OPEN_ROOM_ERROR;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final MemberJpaRepository memberJpaRepository;


    @Transactional
    @Override
    public ChatRoomResponseDto createChatRoom(ChatRoomReqDTO chatRoomReqDTO) {
        // request 자체가 빈 경우
        if (chatRoomReqDTO == null) throw new CustomException(ErrorCode.REQUEST_EMPTY);
        // 비밀방 선택 - 비밀번호 입력하지 않은 경우 오류 발생
        if (!chatRoomReqDTO.getOpen() && (chatRoomReqDTO.getPassword().isEmpty())) throw new CustomException(CLOSE_ROOM_ERROR);
        // 공개방 선택 - 비밀번호 입력한 경우 오류 발생
        if (chatRoomReqDTO.getOpen() && (!chatRoomReqDTO.getPassword().isEmpty())) throw new CustomException(OPEN_ROOM_ERROR);

        ChatRoom chatRoom = chatRoomJpaRepository.save(createToEntity(chatRoomReqDTO));

        // 채팅방 생성하기
        return ChatRoomResponseDto.of(chatRoom);
    }

    /**
     * 입장 가능한 채팅방 보여주기 (open 이면서 대기 중인 방 , 빠른 시작 가능한 방 순으로 정렬)
     */
    @Override
    public Page<ChatRoomListResponseDto> findAllChatRooms(int pageNumber, int size) {

        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<ChatRoom> allByOpenTrueAndState = chatRoomJpaRepository.findAllByOpenTrueAndState(ChatRoomCode.WAITING, pageable);
        return allByOpenTrueAndState.map(ChatRoomListResponseDto::fromEntity);

    }

    /**
     * 채팅방 이름으로 찾기
     * */
    @Override
    public List<ChatRoomListResponseDto> findChatRoomByTitle(String title) {
        List<ChatRoom> chatRooms = chatRoomJpaRepository.findAll().stream()
                .filter(chatRoom -> chatRoom.getTitle().contains(title))
                .collect(Collectors.toList());

        return chatRooms.stream()
                .map(ChatRoomListResponseDto::fromEntity)
                .collect(Collectors.toList());

    }

    /**
     * 방장 이름으로 찾기
     */
    @Override
    public List<ChatRoomListResponseDto> findChatRoomByNickname(String nickname) {
        // 닉네임이 포함된 멤버 모두 찾기 (대 소문자 구분하지 않음)
        List<Member> members = memberJpaRepository.findAllByMemberNameContainingIgnoreCase(nickname)
                .get().stream()
                .collect(Collectors.toList());

        List<ChatRoomListResponseDto> chatRoomResponseDtoList = new ArrayList<>();

        for (Member member : members) {
            Optional<List<ChatRoom>> optionalChatRoomList = chatRoomJpaRepository.findAllByOwnerId(member.getId());
            if (optionalChatRoomList.isPresent()) {
                for (ChatRoom chatRoom : optionalChatRoomList.get()) {
                    chatRoomResponseDtoList.add(ChatRoomListResponseDto.fromEntity(chatRoom));
                }
            }

        }

        return chatRoomResponseDtoList;
    }

    // 채팅방 수정
    @Transactional
    @Override
    public ChatRoomResponseDto updateChatRoom(ChatRoomUpdateReqDto dto, Long id){
        // 기존 채팅방 데이터 받기
        ChatRoom findChatRoom = chatRoomJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROOM));
        // 수정을 요청한 사용자와 방장이 일치하는지 확인
        if(findChatRoom.getOwnerId() != dto.getMemberId()) throw new CustomException(ErrorCode.USER_NOT_OWNER);
        // 데이터 수정
        findChatRoom.modify(updateToEntity(dto, id));
        // 비밀방 선택 - 비밀번호 입력하지 않은 경우라면
        if (!findChatRoom.getOpen() && findChatRoom.getPassword() == null) throw new CustomException(ErrorCode.PASSWORD_EMPTY);
        // 공개방 선택 - 비밀번호 입력한 경우라면
        if (findChatRoom.getOpen() && findChatRoom.getPassword() != null) throw new CustomException(ErrorCode.OPEN_PASSWORD);
        // 수정한 데이터 반환
        return ChatRoomResponseDto.of(findChatRoom);
    }

    // 채팅방 삭제
    @Transactional
    @Override
    public void deleteChatRoom(Long memberId, Long chatRoomId){
        // 기존 채팅방 데이터 받기
        ChatRoom findChatRoom = chatRoomJpaRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROOM));
        // 삭제를 요청한 사용자와 방장이 일치하는지 확인
        if(findChatRoom.getOwnerId() != memberId) throw new CustomException(ErrorCode.USER_NOT_OWNER);
        // 삭제
        chatRoomJpaRepository.delete(findChatRoom);
    }
}
