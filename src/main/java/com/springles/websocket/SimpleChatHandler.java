package com.springles.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
// TextWebSocketHandler: Websocket 통신을 통해 문자 데이터만 주고받을 때 활용할 수 있는 WebSocketHandler 구현체
public class SimpleChatHandler extends TextWebSocketHandler {
    // 서버에 접속한 사용자들을 관리하기 위한 리스트
    // 하나의 WebSocketSession 객체가 WebSocket 통신을 진행하는 사용자 하나
    private final List<WebSocketSession> sessions = new ArrayList<>();

    // 클라이언트가 WebSocket 연결을 구성할 때 호출됨
    // 연결된 클라이언트를 나타내는 WebSocketSession 객체를 전달해 줌
    // 연결한 클라이언트에게 다시 메시지를 보낼 때 여기서 만든 WebSocketSession 객체를 사용
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 사용자 목록에 사용자 추가
        sessions.add(session);
        // 누가 들어왔고 총 몇 명인지 기록
        log.info("{} connected, total sessions: {}", session, sessions.size());
    }

    // 클라이언트에서 먼저 데이터를 보냈을 때 호출되는 메소드
    // WebSocketSession: 메시지를 보낸 클라이언트
    // TestMessage: 전달한 데이터
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 전달받은 텍스트메시지 꺼내기
        String payload = message.getPayload();
        // 로그로 기록
        log.info("received: {}", payload);
        // sessions 리스트에 저장되어 있는 모든 WebSocketSession에게 전달
        for (WebSocketSession connected: sessions) {
            connected.sendMessage(message);
        }
    }

    // 연결이 종료되면 호출
    // sessions에서 정보를 삭제
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("connection with {} closed", session);
        sessions.remove(session);
    }
}
