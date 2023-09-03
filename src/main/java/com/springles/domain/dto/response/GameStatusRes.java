package com.springles.domain.dto.response;

import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GameStatusRes {

    private GameStatus gameStatus;
    private Map<Long, PlayerStatus> playerMap;

    public static GameStatusRes of(GameSession gameSession, List<Player> players) {
        GameStatusRes gameStatusRes = new GameStatusRes();
        gameStatusRes.gameStatus = GameStatus.of(gameSession);
        gameStatusRes.playerMap = new HashMap<>();

        players.forEach(player -> gameStatusRes.playerMap.put(player.getMemberId(), PlayerStatus.of(player)));
        return gameStatusRes;
    }

}
