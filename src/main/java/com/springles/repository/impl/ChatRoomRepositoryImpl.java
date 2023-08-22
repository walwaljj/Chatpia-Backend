package com.springles.repository.impl;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import com.springles.repository.custom.ChatRoomCustomRepository;
import com.springles.repository.support.Querydsl4RepositorySupport;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.springles.domain.entity.QChatRoom.chatRoom;

@Repository
@Transactional
@Slf4j
public class ChatRoomRepositoryImpl extends Querydsl4RepositorySupport implements ChatRoomCustomRepository {

    public ChatRoomRepositoryImpl() {
        super(ChatRoom.class);
    }

    @Override
    public Optional<List<ChatRoom>> findAllByOwnerId(Long ownerId) {
        List<ChatRoom> chatRoomList = selectFrom(chatRoom)
                .where(chatRoom.ownerId.eq(ownerId))
                .fetch();
        return Optional.of(chatRoomList);

    }

    @Override // 현재 생성순으로 정렬되고 있어 수정 예정
    public Page<ChatRoom> findAllByCloseFalseAndState(ChatRoomCode chatRoomCode, Pageable pageable) {
        List<ChatRoom> chatRoomList = selectFrom(chatRoom)
                .where(
                        chatRoom.close.isFalse(),
                        chatRoom.state.eq(chatRoomCode.WAITING)
                )
                .orderBy(chatRoom.capacity.subtract(chatRoom.head).asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(chatRoomList, pageable, chatRoomList.size());
    }

}
