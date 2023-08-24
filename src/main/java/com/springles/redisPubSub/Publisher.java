package com.springles.redisPubSub;

import com.springles.domain.dto.chatting.MessageRequestDto;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class Publisher {

    private final RedisTemplate<String, Object> redisTemplate;

    // 게임 시작
    public void publish(ChannelTopic topic, String roomId) {

    }

    // Topic별 메세지
    public void publish(ChannelTopic topic, MessageRequestDto messageRequestDto) {

    }
}
