package com.springles.controller.message;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.chatroom.ChatRoomResponseDto;
import com.springles.domain.entity.ChatRoom;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.game.ChatMessage;
import com.springles.game.GameSessionManager;
import com.springles.game.MessageManager;
import com.springles.service.ChatRoomService;
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
    private final ChatRoomService chatRoomService;

    // 메세지 전송
    @MessageMapping("/pub/chat/{roomId}")
    public void sendMessage(SimpMessageHeaderAccessor accessor, String message,
        @DestinationVariable Long roomId) {
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        if (gameSession.getGamePhase().equals(GamePhase.NIGHT_VOTE)) {
            if (gameSessionManager.findPlayerByMemberName(
                accessor.getUser().getName()).getRole().equals(GameRole.MAFIA))
            {
                messageManager.sendMessage("/sub/chat/"+roomId+"/"+"mafia", message, roomId,
                    accessor.getUser().getName());
            }
            return;
        }
        messageManager.sendMessage("/sub/chat/" + roomId, message, roomId,
            accessor.getUser().getName());
    }

    // 게임 생성
    @MessageMapping("/pub/gameCreate/{roomId}")
    public void sendMessage_GameCreate(@DestinationVariable Long roomId) {
        gameSessionManager.createGame(roomId);
    }

    // 게임 참여
    @MessageMapping("/pub/gameJoin/{roomId}")
    public void sendMessage_GameJoin(SimpMessageHeaderAccessor accessor,
        @DestinationVariable Long roomId) {
        String memberName = accessor.getUser().getName();
        gameSessionManager.addUser(roomId, memberName);
        messageManager.sendMessage(
            "/sub/chat/" + roomId,
            memberName + "님이 입장하셨습니다.",
            roomId, "admin");
    }

    // 게임 나가기
    @MessageMapping("/pub/gameExit/{roomId}")
    public void sendMessage_GameExit(SimpMessageHeaderAccessor accessor,
        @DestinationVariable Long roomId) {
        String memberName = accessor.getUser().getName();
        gameSessionManager.removePlayer(roomId, memberName);
        messageManager.sendMessage(
            "/sub/chat/" + roomId,
            memberName + "님이 퇴장하셨습니다.",
            roomId, "admin"
        );
    }

    // 게임 시작
    @MessageMapping("/pub/gameStart/{roomId}")
    public void sendMessage_GameStart(SimpMessageHeaderAccessor accessor,
        @DestinationVariable Long roomId) {

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
                "/sub/chat/" + roomId + "/" + m.getMemberId(),
                "마피아 플레이어는" + " [" + mafiaListString + "] " + "입니다.",
                roomId, "admin"
            );
        });
    }

    // 게임 정보 수정?
    @MessageMapping("/pub/gameUpdate/{roomId}")
    public void sendMessage_GameUpdate(SimpMessageHeaderAccessor accessor,
        @DestinationVariable Long roomId) {
        messageManager.sendMessage(
            "/sub/chat/"+roomId,
            "게임 정보가 변경되었습니다.",
            roomId, "admin"
        );
    }
}
