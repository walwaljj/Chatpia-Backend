package com.springles.domain.dto.vote;

import lombok.Getter;

import java.util.Map;

@Getter
public class VoteResultResponseDto {
    private Map<Long, Long> playerMap;

    public static VoteResultResponseDto of(Map<Long, Long> voteResult) {
        VoteResultResponseDto voteResultResponseDto = new VoteResultResponseDto();
        voteResultResponseDto.playerMap = voteResult;
        return voteResultResponseDto;
    }
}
