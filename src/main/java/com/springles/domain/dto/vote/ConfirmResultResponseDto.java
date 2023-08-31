package com.springles.domain.dto.vote;

import lombok.Getter;

import java.util.Map;

@Getter
public class ConfirmResultResponseDto {
    private Map<Long, Boolean> playerMap;

    public static ConfirmResultResponseDto of(Map<Long, Boolean> confirmResult) {
        ConfirmResultResponseDto confirmResultResponseDto = new ConfirmResultResponseDto();
        confirmResultResponseDto.playerMap = confirmResult;
        return confirmResultResponseDto;
    }
}
