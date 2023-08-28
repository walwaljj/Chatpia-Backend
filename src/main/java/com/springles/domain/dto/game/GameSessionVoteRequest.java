package com.springles.domain.dto.game;

import com.springles.domain.constants.GamePhase;
import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSessionVoteRequest {
    @Enumerated(EnumType.STRING)
    private GamePhase phase;

    @Nullable
    String vote;
}
