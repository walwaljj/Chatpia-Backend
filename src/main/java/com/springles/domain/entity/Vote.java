package com.springles.domain.entity;

import com.springles.domain.constants.GamePhase;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

// Vote 자체는 Redis에 저장하지 않고 Vote 모음집을 HashOperations를 통해 CRUD 하는 방향
@Builder
@Getter
@Setter
public class Vote implements Serializable { // 외부로 전송하기 위한 직렬화
    @Id
    private Long playerId;

    @Enumerated(EnumType.STRING)
    private GamePhase phase;

    private Long vote;

    private boolean confirm;

    public static Vote builder(Long playerId, GamePhase phase) {
        return new VoteBuilder().playerId(playerId).phase(phase).vote(null).confirm(false).build();
    }
}
