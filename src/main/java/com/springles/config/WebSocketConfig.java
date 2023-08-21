package com.springles.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // configureMessageBroker: 메시지를 주고받는 미들웨어를 Message Brocker라고 하는데
    // Message Brocker를 사용하는 방법
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독하는 요청 url -> 메시지를 받을 때
        // 사용자가 받을 데이터를 분류하기 위한 경로
        registry.enableSimpleBroker("/sub");

        // 메시지를 발행하는 요청 url -> 메시지를 보낼 때
        // 사용자가 보내는 데이터가 도달할 경로
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // STOMP 엔드포인트 설정용 메소드
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp 접속 요청 url
        registry.addEndpoint("/chat")
            .setAllowedOriginPatterns("*");
    }
}