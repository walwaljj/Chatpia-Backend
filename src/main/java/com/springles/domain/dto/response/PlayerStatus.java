package com.springles.domain.dto.response;

import com.springles.domain.entity.Player;
import lombok.Getter;

@Getter
public class PlayerStatus {
    boolean alive;

    public static PlayerStatus of(Player player) {
        PlayerStatus playerStatus = new PlayerStatus();
        playerStatus.alive = player.isAlive();
        return playerStatus;
    }
}
