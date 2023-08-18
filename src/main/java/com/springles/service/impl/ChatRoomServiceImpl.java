package com.springles.service.impl;


import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.dto.chatting.ChatRoomListResponseDto;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.entity.ChatRoom;
import com.springles.domain.entity.Member;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.redis.Redisroom;
import com.springles.repository.ChatRoomJpaRepository;
import com.springles.repository.ChatRoomRedisRepository;
import com.springles.repository.MemberRepository;
import com.springles.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.springles.domain.dto.chatroom.ChatRoomReqDTO.createToEntity;
import static com.springles.exception.constants.ErrorCode.CLOSE_ROOM_ERROR;
import static com.springles.exception.constants.ErrorCode.OPEN_ROOM_ERROR;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ChatRoomRedisRepository chatRoomRedisRepository;
    private final MemberRepository memberRepository;

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

        // Redis에 방 생성
        Redisroom redisroom = Redisroom.builder()
            .id(chatRoom.getId()+"")
            .topic(chatRoom.getId()+"")
            .build();

        log.info(chatRoomRedisRepository.save(redisroom).toString());
        log.info(redisroom.getId());

        // 채팅방 생성하기
        return ChatRoomResponseDto.of(chatRoom);
    }

    /**
     * 입장 가능한 채팅방 보여주기 (open 이면서 대기 중인 방 , 빠른 시작 가능한 방 순으로 정렬)
     */
    @Override
    public Page<ChatRoomListResponseDto> findAllChatRooms(int pageNumber, int size) {
        Pageable pageable = PageRequest.of(pageNumber, size);

        List<ChatRoomListResponseDto> ChatRoomResponseDtoList = chatRoomJpaRepository.findAllByOpenTrueAndState(ChatRoomCode.WAITING)
                .get()
                .stream()
                .sorted(Comparator.comparingLong(o -> (o.getCapacity() - o.getHead())))
                .map(ChatRoomListResponseDto::fromEntity).collect(Collectors.toList());

        return new PageImpl<>(ChatRoomResponseDtoList, pageable, ChatRoomResponseDtoList.size());

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
        List<Member> members = memberRepository.findAllByMemberNameContainingIgnoreCase(nickname)
                .get().stream()
                .collect(Collectors.toList());

        List<ChatRoomListResponseDto> chatRoomResponseDtoList = new ArrayList<>();

        for (Member member : members) {
            Optional<ChatRoom> optionalChatRoom = chatRoomJpaRepository.findAllByOwnerId(member.getId());
            if(optionalChatRoom.isPresent())
                chatRoomResponseDtoList.add(ChatRoomListResponseDto.fromEntity(optionalChatRoom.get()));
        }

        return chatRoomResponseDtoList;
    }
}
