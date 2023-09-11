package com.springles.controller.message;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.message.RoleExplainMessage;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.exception.GameEndException;
import com.springles.game.GameSessionManager;
import com.springles.game.MessageManager;
import com.springles.service.ChatRoomService;
import com.springles.service.MemberService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.TaskQueue;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameSessionManager gameSessionManager;
    private final MessageManager messageManager;
    private final ChatRoomService chatRoomService;
    private final MemberService memberService;

    /*메세지 전송*/
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(SimpMessageHeaderAccessor accessor, String message,
        @DestinationVariable Long roomId) {

        log.info("수신 메시지: " + message + " 방 id: " + roomId + " 발송자: " + getMemberName(accessor));

        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        Player player = gameSessionManager.findPlayerByMemberName(getMemberName(accessor));
        // 관전자는 관전자들끼리만 채팅이 가능
        if (player.getRole().equals(GameRole.OBSERVER)) {
            messageManager.sendMessage("/sub/chat/" + roomId + "/" + GameRole.OBSERVER, message,
                roomId,
                player.getMemberName());
            return;
        }
        // 밤 투표시간에는 마피아끼리만 채팅 가능
        if (gameSession.getGamePhase().equals(GamePhase.NIGHT_VOTE)) {
            if (player.getRole().equals(GameRole.MAFIA)) {
                messageManager.sendMessage("/sub/chat/" + roomId + "/" + GameRole.MAFIA, message,
                    roomId,
                    player.getMemberName());
            }
            return;
        }
        // 위의 모든 조건이 아니라면 방에 참여한 모두에게 메시지 전송
        messageManager.sendMessage("/sub/chat/" + roomId, message, roomId,
            player.getMemberName());
    }

    /*게임 생성*/
    @MessageMapping("/gameCreate/{roomId}")
    public void sendMessage_GameCreate(@DestinationVariable Long roomId) {
        log.info("게임 생성");
        gameSessionManager.createGame(roomId);
    }

    /*게임 참여*/
    @MessageMapping("/gameJoin/{roomId}")
    public void sendMessage_GameJoin(SimpMessageHeaderAccessor accessor,
        @DestinationVariable Long roomId) {
        String memberName = getMemberName(accessor);

        // 게임 참여자 목록 갱신
        messageManager.sendMessage(
            "/sub/chat/" + roomId + "/" + "playerList",
            gameSessionManager.addUser(roomId, memberName));

        // 게임 참여 메시지 전송
        messageManager.sendMessage(
            "/sub/chat/" + roomId,
            memberName + "님이 입장했습니다.",
            roomId, "admin");
    }

    /*게임 나가기*/
    @MessageMapping("/gameExit/{roomId}")
    public void sendMessage_GameExit(SimpMessageHeaderAccessor accessor,
        @DestinationVariable Long roomId) {
        String memberName = getMemberName(accessor);
        gameSessionManager.removePlayer(roomId, memberName);

        // 게임 참여자 목록 갱신
        messageManager.sendMessage(
            "/sub/chat/" + roomId + "/" + "playerList",
            gameSessionManager.findPlayersByRoomId(roomId)
        );

        // 게임 퇴장 메시지 전송
        messageManager.sendMessage(
            "/sub/chat/" + roomId,
            memberName + "님이 퇴장했습니다.",
            roomId, "admin"
        );
    }

    /*게임 정보 수정*/
    @MessageMapping("/gameUpdate/{roomId}")
    public void sendMessage_GameUpdate(SimpMessageHeaderAccessor accessor,
        @DestinationVariable Long roomId) {
        messageManager.sendMessage(
            "/sub/chat/" + roomId,
            "게임 정보가 변경되었습니다.",
            roomId, "admin"
        );
    }

    /*게임 시작*/
    @MessageMapping("/gameStart/{roomId}")
    public void sendMessage_GameStart(SimpMessageHeaderAccessor accessor,
        @DestinationVariable Long roomId) {
        if (!startGame(accessor, roomId)) {
            return;
        }
        while (true) {
            try {
                check_Game_Ending(roomId); // 게임 종료 조건 검사
                day_Discussion(roomId); // 낮 토의 시간 시작
                day_First_Vote(roomId); // 낮 토의 60초 뒤에 투표 시작
                vote_End(roomId); // 낮 투표 10초 뒤에 투표 마감 -> 투표 확정, 프론트 투표 못하게 막기
                log.info("투표 결과 집계"); // 낮 결과 집계 및 발표

                // 낮 투표 결과 죽을 사람이 있다면
                day_Final_Speech(roomId); // 변론 시작
                day_Second_Vote(roomId); // 변론 30초 후 투표 진행
                vote_End(roomId); // 10초 뒤에 투표 마감 -> 투표 확정, 프론트 투표 못하게 막기
                log.info("투표 결과 집계 및 처리");  // 결과 집계 및 발표
                check_Game_Ending(roomId); // 게임 종료 조건 검사

                night_Vote(roomId); // 밤 투표 진행
                vote_End(roomId); // // 밤 투표 20초 뒤에 투표 마감 -> 투표 확정, 프론트 투표 못하게 막기
                log.info("투표 결과 집계 및 처리");  // 결과 집계 및 발표

            } catch (GameEndException e) { // 정상적인 게임 종료 -> 결과 출력
                endGame(roomId);
                break;
            } catch (Exception e) { // 서버 에러로 게임 종료 -> 에러 원인 출력
                endWithError(roomId);
                break;
            }
            break; // 임시 break
        }
    }

    private boolean startGame(SimpMessageHeaderAccessor accessor, Long roomId) {
        log.info("요청자 정보: " + getMemberName(accessor));

        // 방장만 게임을 시작 가능
        if (!Objects.equals(memberService.findUserByName(getMemberName(accessor)).getId(),
            chatRoomService.findChatRoomByChatRoomId(roomId).getOwnerId())) {
            messageManager.sendMessage(
                "/sub/chat/" + roomId + "/" + getMemberName(accessor),
                "방장만 게임을 시작할 수 있습니다.", roomId, "admin");
            return false;
        }

        // 참여자 수가 5이상 10 이하여야 함.
        int playerSize = gameSessionManager.findPlayersByRoomId(roomId).size();
        if (playerSize < 5 || playerSize > 10) {
            messageManager.sendMessage(
                "/sub/chat/" + roomId + "/" + getMemberName(accessor),
                "참여자 수가 부족합니다.", roomId, "admin");
            return false;
        }

        // 게임 시작 메시지 출력
        messageManager.sendMessage("/sub/chat/" + roomId,
            "게임이 시작되었습니다.",
            roomId, "admin");

        // 게임 시작 -> 직업 랜덤 부여, 게임 페이즈 변경, 직업 설명
        List<Player> mafiaList = new ArrayList<>();
        gameSessionManager.startGame(roomId, getMemberName(accessor)).forEach(p -> {
            messageManager.sendMessage(
                "/sub/chat/" + roomId + "/gameRole/" + p.getMemberName(),
                new RoleExplainMessage(p.getRole(), getTimeString())
            );
            if (p.getRole().equals(GameRole.MAFIA)) {
                mafiaList.add(p);
            }
        });

        // 마피아들에게 마피아가 누구인지 알려주기
        String mafiaListString = mafiaList.stream()
            .map(Player::getMemberName)
            .collect(Collectors.joining(", "));
        mafiaList.forEach(m -> {
            messageManager.sendMessage(
                "/sub/chat/" + roomId + "/" + m.getMemberName(),
                "마피아 플레이어는" + " [" + mafiaListString + "] " + "입니다.",
                roomId, "admin"
            );
        });

        return true;
    }
    private void day_Discussion(Long roomId) throws InterruptedException {
        // 낮 시작 안내 메시지
        messageManager.sendMessage(
            "/sub/chat/" + roomId,
            gameSessionManager.findGameByRoomId(roomId).getDay() + "번 째 낮이 밝았습니다.",
            roomId, "admin"
        );
        TimeUnit.SECONDS.sleep(10);
    }
    private void day_First_Vote(Long roomId) throws InterruptedException {
        // 낮 투표 시작 메시지
        messageManager.sendMessage(
            "/sub/chat/" + roomId,
            "투표가 시작 되었습니다. 마피아라고 생각되는 플레이어에게 투표해주세요.",
            roomId, "admin"
        );
        TimeUnit.SECONDS.sleep(10);
    }
    private void day_Final_Speech(Long roomId) {

    }
    private void day_Second_Vote(Long roomId) {
        // 마지막 투표 시작 메시지

        // 마지막 투표 시간 (10초)

        // 투표 종료 메시지

        // 마지막 투표 결과 집계

        // 결과 반영
    }
    private void night_Vote(Long roomId) {
        // 밤 시작 메시지(투표+토의)

        // 밤 투표 종료 메시지

        // 밤 투표 결과 집계

        // 밤 투표 결과 반영
    }
    private void check_Game_Ending(Long roomId) throws InterruptedException {
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        // 마피아가 다 죽으면 시민팀 승리
        if (gameSession.getAliveMafia() <= 0) {
            throw new GameEndException();
        }
        // 마피아가 더 많으면 마피아 승리
        if (gameSession.getAliveMafia()
            >= gameSession.getAliveDoctor() + gameSession.getAlivePolice()
            + gameSession.getAliveCivilian()) {
            throw new GameEndException();
        }
        TimeUnit.SECONDS.sleep(1);
    }
    private void vote_End(Long roomId) throws InterruptedException {
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        gameSession.setGamePhase(GamePhase.VOTE_END);
        gameSessionManager.saveSession(gameSession);
        // 투표 종료 메시지
        messageManager.sendMessage(
            "/sub/chat/" + roomId,
            "투표가 종료 되었습니다.",
            roomId, "admin"
        );
        // 프론트에서 투표를 막도록 설정
        messageManager.sendMessage("/sub/voteEnd/" + roomId, null);

        TimeUnit.SECONDS.sleep(4);
    }
    private void endGame(Long roomId) {
        // 게임 종료 메시지 출력

        // 게임 결과 메시지 출력
    }
    private void endWithError(Long roomId) {
        // exception 내용 메시지로 출력
        // 게임 종료
    }

    private String getMemberName(SimpMessageHeaderAccessor accessor) {
        return accessor.getUser().getName().split(",")[1].split(":")[1].trim();
    }
    private String getTimeString() {
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

}
