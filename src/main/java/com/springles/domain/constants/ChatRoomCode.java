package com.springles.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatRoomCode implements BaseEnumCode<String>{
    WAITING("대기중"),
    PLAYING("진행중");

    private final String value;
}
