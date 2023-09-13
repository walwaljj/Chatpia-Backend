package com.springles.service;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.vote.GameSessionVoteRequestDto;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface GameSessionVoteService {
    void startVote (Long roomId, int phaseCount, GamePhase phase, LocalDateTime time, Map<Long, GameRole> players);
    Map<Long, Long> endVote (Long roomId, int phaseCount, GamePhase phase);
    Map<Long, Long> vote (Long roomId, Long playerId, GameSessionVoteRequestDto request);
    Map<Long, Long> nightVote (Long roomId, Long playerId, GameSessionVoteRequestDto request, GameRole role);
    Map<Long, Long> getVoteResult(Long roomId, GameSessionVoteRequestDto request);

    Map<Long, Long> getVotePossible(Long roomId);

    Map<Long, Player> getSuspectResult(GameSession gameSession, Map<Long, Long> vote);

    List<Long> getSuspiciousList(GameSession gameSession, Map<Long, Long> voteResult);

    Long getEliminationPlayer(GameSession gameSession, Map<Long, Long> voteResult);

    Map<GameRole, Long> getNightVoteResult(GameSession gameSession, Map<Long, Long> voteResult);
}
