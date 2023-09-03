package com.springles.domain.dto.response;

import com.springles.domain.constants.GameRole;
import lombok.Getter;

@Getter
public class PlayerDeadRes {

    private GameRole role;

    public static PlayerDeadRes of() {
        PlayerDeadRes playerRoleRes = new PlayerDeadRes();
        playerRoleRes.role = GameRole.OBSERVER;
        return playerRoleRes;
    }
}
