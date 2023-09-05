
package com.springles.websocket;

import com.springles.game.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

//@Slf4j
//@Controller
//public class WebSocketMapping {
//    // @EnableWebSocketMessageBroker 를 설정할 경우 자동으로 설정되는 Bean 이며, 연결된 클라이언트에 데이터를 전송하기 위해 사용
//    // 특정 Broker로 메시지를 전달
//    private final SimpMessagingTemplate simpMessagingTemplate;
//
//    public WebSocketMapping(SimpMessagingTemplate simpMessagingTemplate) {
//        this.simpMessagingTemplate = simpMessagingTemplate;
//    }
//
//    // STOMP 엔드포인트를 구성할때는 @RequestMapping 대신 @MessageMapping 을 사용
//    // 전달하는 /chat 인자는 이전에 Configuration 구성 중 작성한 /app 뒤에 붙임
//    // 즉 사용자가 STOMP 클라이언트를 사용하면 /app/chat 으로 요청 전송
//    @MessageMapping("/chatting")
//    public void sendChat(ChatMessage chatMessage){
//        log.info(chatMessage.toString());
//        String time = new SimpleDateFormat("HH:mm").format(new Date());
//        chatMessage.setTime(time);
//        simpMessagingTemplate.convertAndSend(
//                String.format("/sub/chat/%s", chatMessage.getRoomId()),
//                chatMessage
//        );
//    }
//}
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketMapping {
    // STOMP over WebSocket 요청을 보낼 때 사용하는 용도
    // session에 메시지를 썼던 것처럼
    private final SimpMessagingTemplate simpMessagingTemplate;

    // RequestMapping 에 대응
    @MessageMapping("/chat")
    public void sendChat(
            ChatMessage chatMessage,
            @Headers Map<String, Object> headers,
            @Header("nativeHeaders") Map<String, Object> nativeHeaders
    ) {
        log.info(chatMessage.toString());
        log.info(headers.toString());
        log.info(nativeHeaders.toString());
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        chatMessage.setTime(time);
        simpMessagingTemplate.convertAndSend(
                String.format("/topic/%s", chatMessage.getRoomId()),
                chatMessage
        );
    }
}