package com.springles.controller.message;

import com.springles.domain.constants.GameRole;
import com.springles.domain.entity.Player;
import com.springles.game.ChatMessage;
import com.springles.game.GameSessionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import groovy.util.logging.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameSessionManager gameSessionManager;

    // 메세지 전송
    @MessageMapping("/pub/chat/{roomId}")
    public void sendMessage(SimpMessageHeaderAccessor accessor,String message, @DestinationVariable Long roomId) {
        /*
         * 밤에 시민이 채팅을 하지 못하게 하는 방법?
         * 최후의 변론 때 대상자를 제외하곤 채팅을 하지 못하게 하는 방법?
         * */
        simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId,
            ChatMessage.builder()
                .message(message)
                .sender(accessor.getUser().getName())
                .time(getTimeString())
                .roomId(roomId)
                .build());
    }

    // 게임 시작
    @MessageMapping("/pub/gameStart/{roomId}")
    public void sendMessage_GameStart(@DestinationVariable Long roomId) {
        /*
         * 모든 플레이어에게 게임시작 메세지를 보냄.
         * 각 플레이어마다 직업을 설명해줌.
         * 마피아는 누가 마피아인지 함께 설명해줌.
         * */
        ChatMessage gameStartMessage = ChatMessage.builder()
            .message("게임이 시작되었습니다.")
            .sender("admin")
            .time(getTimeString())
            .build();

        simpMessagingTemplate.convertAndSend("/sub/gameStart/" + roomId, gameStartMessage);

        List<Player> mafiaList = new ArrayList<>();
        gameSessionManager.startGame(roomId).forEach(p -> {
            simpMessagingTemplate.convertAndSend(
                "/sub/chat/explainRole/" + p.getMemberId(),
                ChatMessage.builder()
                    .message("당신은 " + p.getRole() + "입니다.")
                    .sender("admin")
                    .time(getTimeString())
                    .roomId(p.getRoomId())
                    .build()
            );
            if (p.getRole().equals(GameRole.MAFIA)) mafiaList.add(p);
        });
        String mafiaListString = mafiaList.stream()
            .map(Player::getMemberName)
            .collect(Collectors.joining(", "));

        mafiaList.forEach(m -> {
            simpMessagingTemplate.convertAndSend("/sub/player/"+m.getMemberId(),
                ChatMessage.builder()
                    .message("마피아 플레이어는" + " [" + mafiaListString + "] " + "입니다."));
        });
    }

    // 게임 참여
    @MessageMapping("/pub/joinGame/{roomId}")
    public void sendMessage_JoinGame(SimpMessageHeaderAccessor accessor, @DestinationVariable Long roomId) {
        /*
         * 게임 참여 메세지를 모두에게 전송함.
         * */
        String memberName = accessor.getUser().getName();
        gameSessionManager.addUser(roomId, memberName);
        simpMessagingTemplate.convertAndSend("/sub/joinGame/" + roomId,
            ChatMessage.builder()
                .message(memberName + "님이 입장하셨습니다.")
                .sender("admin")
                .time(getTimeString())
                .roomId(roomId)
                .build());
    }

    // 게임 나가기
    @MessageMapping("/pub/exitGame/{roomId}")
    public void sendMessage_ExitGame(SimpMessageHeaderAccessor accessor, @DestinationVariable Long roomId) {
        /*
         * 게임 나가기 메세지를 모두에게 전송함.
         * */
        String memberName = accessor.getUser().getName();
        gameSessionManager.removePlayer(roomId, memberName);
        simpMessagingTemplate.convertAndSend("/sub/exitGame/" + roomId,
            ChatMessage.builder()
                .message(memberName+"님이 퇴장하셨습니다.")
                .sender("admin")
                .time(getTimeString())
                .roomId(roomId)
                .build());
    }

    // 게임 페이즈 변경

    // 투표 결과

    // 게임 결과

    // 게임 정보 변경

    public String getTimeString() {
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

}
