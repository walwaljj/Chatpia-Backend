package com.springles.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    // registerWebSocketHandlers (어떤 주소에 어떤 핸들러를 활용할지를 정의하는 메소드) 대신
    // STOMP 규약을 사용하는 WebSocket 엔드포인트를 구성하는 메소드
    // 이 메소드를 활용하면 ws://localhost:8080/chat 으로 연결된 통신들이 STOMP 규약을 지켜 메시지 전송
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/chatting").setAllowedOrigins("*");
    }


    // configureMessageBroker 를 통해 목적지와 상세 엔드포인트를 설정
    // 한 클라이언트에서 다른 클라이언트로 메시지를 라우팅 할 때 사용하는 브로커를 구성
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 수신
        // enableSimpleBroker 메소드로 정의된 경로가 클라이언트가 듣기 위한 경로
        // topic이라는 주제를 가진 메시지를 핸들러로 라우팅하여 해당 주제에 가입한 모든 클라이언트에게 메시지를 방송
        registry.enableSimpleBroker("/topic",
                "/sub/chat",
            "/sub/gameStart",
            "/sub/joinGame",
            "/sub/exitGame",
            "/sub/gameRole"
            );

        // 발신
        // setApplicationDestinationPrefixes 다음에 정의할 서버 측 엔드포인트에 대한 Prefix를 설정하는 메소드
        // /app로 시작하는 메시지만 메시지 헨들러로 라우팅한다고 정의
        registry.setApplicationDestinationPrefixes("/pub");
    }

}
