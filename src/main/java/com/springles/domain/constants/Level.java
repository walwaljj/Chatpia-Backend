package com.springles.domain.constants;

import lombok.Getter;

@Getter
public enum Level {
    BEGINNER(0L, 200L, "BEGINNER"),
    ASSOCIATE(1L, 1000L, "ASSOCIATE"),
    SOLDIER(2L, 3000L, "SOLDIER"),
    CAPTAIN(3L, 6000L, "CAPTAIN"),
    UNDERBOSS(4L, 10000L, "UNDER BOSS"),
    BOSS(5L, 0L, "BOSS"),
    NONE(6L,0L,"NONE");

    private Long val;
    private Long goalExp;
    private String name;

    Level(Long val, Long goalExp, String name) {
        this.val = val;
        this.goalExp = goalExp;
        this.name = name;
    }
}
