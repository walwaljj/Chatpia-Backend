package com.springles.domain.dto.response;

import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GameStatusKillRes {

    private GameStatusKill gameStatus;
    private Map<Long, PlayerStatus> playerMap;

    public static GameStatusKillRes of(GameSession gameSession, List<Player> players, Player dead) {
        GameStatusKillRes gameStatusRes = new GameStatusKillRes();
        gameStatusRes.gameStatus = GameStatusKill.of(gameSession, dead);
        gameStatusRes.playerMap = new HashMap<>();
        for (Player player : players) {
            gameStatusRes.playerMap.put(player.getMemberId(), PlayerStatus.of(player));
        }
        return gameStatusRes;
    }

}
