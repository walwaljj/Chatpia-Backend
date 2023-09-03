package com.springles.controller.message;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.entity.Player;
import com.springles.game.ChatMessage;
import com.springles.game.GameSessionManager;
import com.springles.game.MessageManager;
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
    private final MessageManager messageManager;

    // 메세지 전송
    @MessageMapping("/pub/chat/{roomId}")
    public void sendMessage(SimpMessageHeaderAccessor accessor, String message,
        @DestinationVariable Long roomId) {
        /*
         * 밤에 시민이 채팅을 하지 못하게 하는 방법?
         * 최후의 변론 때 대상자를 제외하곤 채팅을 하지 못하게 하는 방법?
         * */
        messageManager.sendMessage("/sub/chat/" + roomId, message, roomId,
            accessor.getUser().getName());
    }

    // 게임 시작
    @MessageMapping("/pub/gameStart/{roomId}")
    public void sendMessage_GameStart(SimpMessageHeaderAccessor accessor,
        @DestinationVariable Long roomId) {
        /*
         * 모든 플레이어에게 게임시작 메세지를 보냄.
         * 각 플레이어마다 직업을 설명해줌.
         * 마피아는 누가 마피아인지 함께 설명해줌.
         * */
        messageManager.sendMessage("/sub/chat/" + roomId,
            "게임이 시작되었습니다.",
            roomId, "admin");

        List<Player> mafiaList = new ArrayList<>();
        gameSessionManager.startGame(roomId,accessor.getUser().getName()).forEach(p -> {
            messageManager.sendMessage(
                "/sub/chat/"+roomId+"/"+p.getMemberId(),
                "당신은 " + p.getRole() + "입니다.",
                roomId, "admin"
                );
            if (p.getRole().equals(GameRole.MAFIA)) mafiaList.add(p);
        });

        String mafiaListString = mafiaList.stream()
            .map(Player::getMemberName)
            .collect(Collectors.joining(", "));

        mafiaList.forEach(m -> {
            messageManager.sendMessage(
                "/sub/chat/" + roomId + "/" + "mafia",
                "마피아 플레이어는" + " [" + mafiaListString + "] " + "입니다.",
                roomId, "admin"
            );
        });
    }

    public String getTimeString() {
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

}
