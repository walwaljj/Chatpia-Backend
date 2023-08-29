package com.springles.service.impl;

import com.springles.config.TimeConfig;
import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.game.GameSessionVoteRequest;
import com.springles.game.task.VoteFinTimerTask;
import com.springles.redisPubSub.Publisher;
import com.springles.repository.VoteRepository;
import com.springles.service.GameSessionVoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Timer;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameSessionVoteServiceImpl implements GameSessionVoteService {

//    private final Publisher redisPublisher;
    private final VoteRepository voteRepository;
//    private final GameSessionService gameSessionService;
//    private final PlayerRedisRepository playerRedisRepository;
//    private final ChannelTopic topicDayDiscussionFin;
//    private final ChannelTopic topicDayEliminationFin;
//    private final ChannelTopic topicNightVoteFin;

    @Override
    public void startVote(Long roomId, int phaseCount, GamePhase phase, LocalDateTime time, Map<Long, GameRole> players) {
        log.info("Room {} start Vote for {}", roomId, phase);
        voteRepository.startVote(roomId, phaseCount, phase, players);
        Timer timer = new Timer();
        VoteFinTimerTask task = new VoteFinTimerTask(this);
        task.setRoomId(roomId);
        task.setPhaseCount(phaseCount);
        task.setPhase(phase);
        timer.schedule(task, TimeConfig.convertToDate(time));
    }

    @Override
    public void endVote(Long roomId, int phaseCount, GamePhase phase) {
        // 이미 끝났다면
        if(voteRepository.isEnd(roomId, phaseCount)) {
            return;
        }
        else {
            Map<Long, Long> vote = voteRepository.getVoteResult(roomId);
            log.info("Room {} end Vote for {}", roomId, phase);
            voteRepository.endVote(roomId, phase);
            publishRedis(roomId, vote);
        }
    }

    @Override
    public Map<Long, Long> vote(Long roomId, Long playerId, GameSessionVoteRequest request) {
        return null;
    }

    @Override
    public Map<Long, Long> nightVote(Long roomId, Long playerId, GameSessionVoteRequest request, GameRole role) {
        return null;
    }

    @Override
    public Map<Long, Boolean> confirmVote(Long roomId, Long playerId, GameSessionVoteRequest request) {
        return null;
    }

    @Override
    public Map<Long, Boolean> getConfirm(Long roomId, Long playerId, GameSessionVoteRequest request) {
        return null;
    }

    @Override
    public Map<Long, Boolean> getNightConfirm(Long roomId, Long playerId, GameSessionVoteRequest request, GameRole role) {
        return null;
    }

    @Override
    public Map<Long, Long> getVoteResult(Long roomId, GameSessionVoteRequest request) {
        return null;
    }

    private void publishRedis(Long roomId, Map<Long, Long> vote) {

    }
}