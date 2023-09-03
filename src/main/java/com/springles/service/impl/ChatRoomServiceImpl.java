package com.springles.service.impl;


import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.entity.ChatRoom;
import com.springles.domain.entity.Member;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.game.GameSessionManager;
import com.springles.game.MessageManager;
import com.springles.repository.ChatRoomJpaRepository;
import com.springles.repository.MemberJpaRepository;
import com.springles.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.springles.domain.dto.chatroom.ChatRoomReqDTO.createToEntity;
import static com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto.updateToEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private GameSessionManager gameSessionManager;
    private MessageManager messageManager;

    // 채팅방 생성
    @Transactional
    @Override
    public ChatRoom createChatRoom(ChatRoomReqDTO chatRoomReqDTO, Long id) {
        // request 자체가 빈 경우
        if (chatRoomReqDTO == null) {
            throw new CustomException(ErrorCode.REQUEST_EMPTY);
        }
        // 비밀방 선택 - 비밀번호 입력하지 않은 경우 오류 발생

        if (chatRoomReqDTO.getClose() && chatRoomReqDTO.getPassword().isEmpty()) throw new CustomException(ErrorCode.PASSWORD_EMPTY);
        // 공개방 선택 - 비밀번호 입력한 경우라면
        if (!chatRoomReqDTO.getClose() && !chatRoomReqDTO.getPassword().isEmpty()) throw new CustomException(ErrorCode.OPEN_PASSWORD);

        ChatRoom chatRoom = chatRoomJpaRepository.save(createToEntity(chatRoomReqDTO, id));

        // 채팅방 생성하기
        return chatRoomJpaRepository.save(createToEntity(chatRoomReqDTO, id));
    }

    /**
     * 입장 가능한 채팅방 보여주기
     */
    @Override
    public List<ChatRoomResponseDto> findAllByCloseFalseAndState() {
        return chatRoomJpaRepository.findAllByCloseFalseAndState(ChatRoomCode.WAITING);
    }


    /**
     * 전체 채팅방 보여주기 (대기 중 , 비밀 방 모두 포함)
     */
    @Override
    public List<ChatRoomResponseDto> findAllChatRooms() {

        List<ChatRoom> findAllChatRooms = chatRoomJpaRepository.findAll();
        return findAllChatRooms.stream().map(ChatRoomResponseDto::of).toList();

    }

    @Override
    public ChatRoomResponseDto chatRoomCondition(Long roomId) {

        ChatRoomResponseDto chatRoomResponseDto = findChatRoomByChatRoomId(roomId);
        // 입장 시도 시 정원이 다 찼을 때
        if (chatRoomResponseDto.getHead() >= chatRoomResponseDto.getCapacity()) {
            throw new CustomException(ErrorCode.GAME_HEAD_FULL);
        }
        // 비밀 방 일 때
        if (chatRoomResponseDto.getClose()) {
            throw new CustomException(ErrorCode.CLOSE_ROOM_ERROR);
        }
        // 이미 진행 중 일 때
        if (chatRoomResponseDto.getState().getValue().equals("PLAYING")) {
            throw new CustomException(ErrorCode.PLAYER_STILL_INGAME);
        }

        return chatRoomResponseDto;
    }

    /**
     * 빠른 입장
     */
    @Override
    public ChatRoomResponseDto quickEnter() {
        List<ChatRoomResponseDto> chatRoomResponseDtoList = chatRoomJpaRepository.findAllByCloseFalseAndState(
            ChatRoomCode.WAITING); // 오픈된 방이고 , 대기중인 리스트
        try {
            return chatRoomResponseDtoList.get(0);// 정원수 - 입장 인원 수 중 가장 상단에 있는 방
        } catch (IndexOutOfBoundsException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_QUICK_ENTRY_ROOM);
        }

    }


    /**
     * 채팅방 이름으로 찾기
     */
    @Override
    public List<ChatRoomResponseDto> findChatRoomByTitle(String title) {
        List<ChatRoom> chatRooms = chatRoomJpaRepository.findAll().stream()
            .filter(chatRoom -> chatRoom.getTitle().contains(title))
            .toList();

        return chatRooms.stream()
            .map(ChatRoomResponseDto::of)
            .collect(Collectors.toList());

    }

    /**
     * 방장 이름으로 찾기
     */
    @Override
    public List<ChatRoomResponseDto> findChatRoomByNickname(String nickname) {
        // 닉네임이 포함된 멤버 모두 찾기 (대 소문자 구분하지 않음)
        List<Member> members = memberJpaRepository.findAllByMemberNameContainingIgnoreCase(
            nickname);

        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();

        for (Member member : members) {
            chatRoomResponseDtoList.addAll(chatRoomJpaRepository.findAllByOwnerId(
                member.getId()));
        }

        return chatRoomResponseDtoList;
    }

    // 채팅방 수정
    @Transactional
    @Override
    public ChatRoomResponseDto updateChatRoom(ChatRoomUpdateReqDto dto, Long id) {
        // 기존 채팅방 데이터 받기
        ChatRoom findChatRoom = chatRoomJpaRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROOM));
        // 수정을 요청한 사용자와 방장이 일치하는지 확인
        if (findChatRoom.getOwnerId() != dto.getMemberId()) {
            throw new CustomException(ErrorCode.USER_NOT_OWNER);
        }
        // 데이터 수정
        findChatRoom.modify(updateToEntity(dto, id));
        // 비밀방 선택 - 비밀번호 입력하지 않은 경우라면
        if (findChatRoom.getClose() && findChatRoom.getPassword() == null) {
            throw new CustomException(ErrorCode.PASSWORD_EMPTY);
        }
        // 공개방 선택 - 비밀번호 입력한 경우라면
        if (!findChatRoom.getClose() && findChatRoom.getPassword() != null) {
            throw new CustomException(ErrorCode.OPEN_PASSWORD);
        }
        // 수정한 데이터 반환
        return ChatRoomResponseDto.of(findChatRoom);
    }

    // 채팅방 삭제
    @Transactional
    @Override
    public void deleteChatRoom(Long memberId, Long chatRoomId) {
        // 기존 채팅방 데이터 받기
        ChatRoom findChatRoom = chatRoomJpaRepository.findById(chatRoomId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROOM));
        // 삭제를 요청한 사용자와 방장이 일치하는지 확인
        if (findChatRoom.getOwnerId() != memberId) {
            throw new CustomException(ErrorCode.USER_NOT_OWNER);
        }
        // 삭제
        chatRoomJpaRepository.delete(findChatRoom);
    }

    /**
     * id 로 채팅방 찾기
     *
     * @param id ChatRoom ID
     */
    public ChatRoomResponseDto findChatRoomByChatRoomId(Long id) {
        return ChatRoomResponseDto.of(chatRoomJpaRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROOM)));
    }

    // 타이틀 + 이름으로 검색
    public List<ChatRoomResponseDto> findAllByTitleAndNickname(String searchContent) {

        List<ChatRoomResponseDto> list = new ArrayList<>();

        // 만약 검색어가 없다면 NO_CONTENT 반환
        if (searchContent.isEmpty()) {
            throw new CustomException(ErrorCode.NO_CONTENT);
        }

        List<ChatRoomResponseDto> chatRoomByNickname = findChatRoomByNickname(searchContent);
        List<ChatRoomResponseDto> chatRoomByTitle = findChatRoomByTitle(searchContent);

        list.addAll(chatRoomByTitle);
        list.addAll(chatRoomByNickname);

        // 검색 결과가 비어있다면 NOT_FOUND_ROOM 반환
        if (list.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_ROOM);
        }

        return list.stream().distinct().toList();

    }

    // 채팅방 입장 시 채팅 정보 받아오기
    public ChatRoomResponseDto enterChatRoom(Long roomId) {
        ChatRoom findChatRoom = chatRoomJpaRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROOM));
        return ChatRoomResponseDto.of(findChatRoom);
    }
}