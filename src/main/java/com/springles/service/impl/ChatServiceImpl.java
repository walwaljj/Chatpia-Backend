package com.springles.service.impl;

import com.springles.domain.dto.chat.MessageRequestDto;
import com.springles.domain.entity.ChatRoom;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.redis.ChatMessage;
import com.springles.redis.ChatMessage.ChatType;
import com.springles.repository.ChatRoomRedisRepository;
import com.springles.redis.Subscriber;
import com.springles.repository.ChatRoomJpaRepository;
import com.springles.service.ChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatServiceImpl implements ChatService {

    private final RedisMessageListenerContainer listenerContainer;
    private final ChatRoomJpaRepository chatRoomRepository;
    private final ChatRoomRedisRepository chatRoomRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Subscriber subscriber;
    private final Topic topic;

    @Override
    public void messageResolver(MessageRequestDto messageRequestDto) {
        ChatRoom room = chatRoomRepository.findById(Long.parseLong(messageRequestDto.getRoomId()))
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_AUTHORIZED_TOKEN) // 임시 -> 에러코드 정의 필요
        );
        sendMessage(ChatMessage.of(messageRequestDto));
    }

    public void sendMessage(ChatMessage message) {
        if (message.getType().equals(ChatType.ENTER)) {
            message.enterMessage();
        } else if (message.getType().equals(ChatType.QUIT)) {
            message.quitMessage();
        }
        log.info("sendMessage = {}", message);
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

}
