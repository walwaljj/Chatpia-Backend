package com.springles.domain.constants;

import lombok.Getter;

@Getter
public enum Level {
//    BEGINNER(0L, 200L, "BEGINNER", "../images/level/BEGINNER.png"),
//    ASSOCIATE(1L, 1000L, "ASSOCIATE", "../images/level/ASSOCIATE.png"),
//    SOLDIER(2L, 3000L, "SOLDIER", "../images/level/SOLDIER.png"),
//    CAPTAIN(3L, 6000L, "CAPTAIN", "../images/level/CAPTAIN.png"),
//    UNDERBOSS(4L, 10000L, "UNDER BOSS", "../images/level/UNDER.png"),
//    BOSS(5L, 0L, "BOSS", "../images/level/BOSS.png"),
//    NONE(6L,0L,"NONE", "");

    BEGINNER(0L, 200L, "BEGINNER", "/images/level/BEGINNER.png"),
    ASSOCIATE(1L, 1000L, "ASSOCIATE", "/images/level/ASSOCIATE.png"),
    SOLDIER(2L, 3000L, "SOLDIER", "/images/level/SOLDIER.png"),
    CAPTAIN(3L, 6000L, "CAPTAIN", "/images/level/CAPTAIN.png"),
    UNDERBOSS(4L, 10000L, "UNDER BOSS", "/images/level/UNDER.png"),
    BOSS(5L, 0L, "BOSS", "/images/level/BOSS.png"),
    NONE(6L,0L,"NONE", "");


    private Long val;
    private Long goalExp;
    private String name;
    private String imgUrl;

    Level(Long val, Long goalExp, String name, String imgUrl) {
        this.val = val;
        this.goalExp = goalExp;
        this.name = name;
        this.imgUrl = imgUrl;
    }
}
