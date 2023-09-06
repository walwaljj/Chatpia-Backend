package com.springles.domain.entity;

import com.springles.config.TimeConfig;
import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRoleNum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@Builder
@RedisHash(value = "GameSession")
public class GameSession {

    @Id
    @Indexed
    private Long roomId; // 방 id로 아이디 설정

    @Indexed
    private Long hostId; // 방장 아이디로도 검색 가능

    @Enumerated(EnumType.STRING)
    private GamePhase gamePhase; // 게임 진행 상태

    private int aliveCivilian; // 살아 있는 시민 수

    private int aliveMafia; // 살아 있는 마피아 수

    private int aliveDoctor; // 살아 있는 의사 수

    private int alivePolice; // 살아 있는 경찰 수

    private int phaseCount; // 게임의 투표 턴 수

    private int day; // 진행된 밤의 수

    private LocalDateTime timer; // 현재 진행 중인 단계 타이머

    public static GameSession of(ChatRoom chatRoom) {
        return GameSession.builder()
            .roomId(chatRoom.getId())
            .hostId(chatRoom.getOwnerId())
            .gamePhase(GamePhase.READY)
            .phaseCount(0)
            .day(0)
            .build();
    }

    public GameSession start(int playerCount) {
        GameRoleNum gameRoleNum = GameRoleNum.getRoleNum(playerCount);
        this.aliveCivilian += gameRoleNum.getCivilian();
        this.aliveMafia += gameRoleNum.getMafia();
        this.aliveDoctor += gameRoleNum.getDoctor();
        this.alivePolice += gameRoleNum.getPolice();
        this.gamePhase = GamePhase.START;
        this.phaseCount = 0;
        this.day = 0;
        return this;
    }

    public void end() {
        this.gamePhase = GamePhase.READY;
        this.aliveCivilian = 0;
        this.aliveMafia = 0;
        this.aliveDoctor = 0;
        this.alivePolice = 0;
        this.phaseCount = 0;
        this.day = 0;
    }

    public void changeHost(long playerId) {
        this.hostId = playerId;
    }

    public void changePhase(GamePhase phase, int timer) {
        this.gamePhase = phase;
        this.phaseCount++;
        this.timer = TimeConfig.getFinTime(timer);
    }

    public void passADay() {
        this.day++;
    }

}
