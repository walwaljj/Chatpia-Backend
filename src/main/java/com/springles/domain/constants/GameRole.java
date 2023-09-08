package com.springles.domain.constants;

import lombok.Getter;
import lombok.val;

@Getter
public enum GameRole {
    MAFIA("마피아"),
    CIVILIAN("시민"),
    POLICE("경찰"),
    DOCTOR("의사"),
    NONE("없음"),
    OBSERVER("관전자");

    GameRole(String val) {
        this.val = val;
    }

    private final String val;
}
