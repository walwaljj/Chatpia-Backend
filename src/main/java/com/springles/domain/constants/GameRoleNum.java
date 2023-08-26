package com.springles.domain.constants;

import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import java.util.EnumMap;
import lombok.Getter;

@Getter
public enum GameRoleNum {
    FIVE(1,3,1,0),
    SIX(1,4,1,0),
    SEVEN(2,3,1,1),
    EIGHT(2,4,1,1),
    NINE(3,3,2,1),
    TEN(3,4,2,1);

    private int mafia, civilian, police, doctor;

    GameRoleNum(int mafia, int civilian, int police, int doctor) {
        this.civilian = civilian;
        this.mafia = mafia;
        this.police = police;
        this.doctor = doctor;
    }

    public static GameRoleNum getRoleNum(int playerCount) {
        return GameRoleNum.values()[playerCount-5];
    }

}
