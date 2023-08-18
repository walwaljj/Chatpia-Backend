package com.springles.config;

import com.springles.redis.ChatMessage;
import com.springles.redis.Subscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@PropertySource("classpath:application-redis.yml")
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    // RedisMessageListenerContainer: redis 채널로부터 메시지를 받는 데 사용
    // 구독자들에게 메시지를 dispatch
    // 메시지를 수신하는 데 관련한 비즈니스 로직 작성
    @Bean
    public RedisMessageListenerContainer listenerContainer(
        RedisConnectionFactory connectionFactory,
        MessageListenerAdapter listenerAdapter,
        ChannelTopic channelTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, channelTopic);
        return container;
    }

    // MessageListenerAdapter: pub sub 모델에서 subscribe 역할 수행
    @Bean
    public MessageListenerAdapter listenerAdapter(Subscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    @Bean
    public RedisTemplate<?,?> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key: String으로 직렬/역직렬화
        // Value: JSON으로 직렬/역직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    // 채널(메시지를 주고받는 공간)을 반환하는 메소드
    // 채널의 이름은 chat
    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("chatroom");
    }



}
