package com.springles.domain.dto.response;

import com.springles.config.TimeConfig;
import com.springles.domain.constants.GamePhase;
import com.springles.domain.entity.GameSession;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameStatus {
    private int day;
    private GamePhase phase;
    private int timer;
    private int aliveMafia;

    public static GameStatus of(GameSession gameSession) {
        GameStatus gameStatus = new GameStatus();
        gameStatus.day = gameSession.getDay();
        gameStatus.phase = gameSession.getGamePhase();
        gameStatus.timer = TimeConfig.getRemainingTime(gameSession.getTimer());
        gameStatus.aliveMafia = gameSession.getAliveMafia();
        return gameStatus;
    }

}
