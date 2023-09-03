package com.springles.domain.dto.response;

import com.springles.config.TimeConfig;
import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameStatusKill {
    private int day;
    private GamePhase phase;
    private int timer;
    private int aliveMafia;
    private Long victim = null;
    private boolean victimIsMafia; // // 투표로 죽은 사람이 마피아인지

    public static GameStatusKill of(GameSession gameSession, Player dead) {
        GameStatusKill gameStatus = new GameStatusKill();
        gameStatus.day = gameSession.getDay();
        gameStatus.phase = gameSession.getGamePhase();
        gameStatus.timer = TimeConfig.getRemainingTime(gameSession.getTimer());
        gameStatus.aliveMafia = gameSession.getAliveMafia();
        if (dead != null) {
            gameStatus.victim = dead.getMemberId();
            gameStatus.victimIsMafia = dead.getRole() == GameRole.MAFIA ? true : false;
        }
        return gameStatus;
    }

}
