package com.springles.game.task;

import com.springles.domain.constants.GamePhase;
import com.springles.service.GameSessionVoteService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.TimerTask;

@RequiredArgsConstructor
@Service
@Setter
public class VoteFinTimerTask extends TimerTask {
    private final GameSessionVoteService gameSessionVoteService;
    private Long roomId;
    private GamePhase phase;
    private int phaseCount;

    @Override
    public void run() {

        gameSessionVoteService.endVote(roomId, phaseCount, phase);
    }
}
