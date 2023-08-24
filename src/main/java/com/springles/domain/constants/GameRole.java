package com.springles.domain.constants;

import lombok.val;

public enum GameRole {
    MAFIA("mafia"),
    CIVILIAN("civilian"),
    POLICE("police"),
    DOCTOR("doctor"),
    OBSERVER("observer"),
    NONE("none");

    GameRole(String val) {
        this.val = val;
    }

    private String val;
}
