package com.springles.repository;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.entity.Vote;
import com.springles.domain.entity.VoteInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Repository
public class VoteRepository {
    private final VoteRedisRepository voteRedisRepository;
    // ConcurrentHashMap: Java에서 제공하는 동시성(Concurrency)을 지원하는 해시 맵(HashMap) 구현체
    private ConcurrentHashMap<Long, VoteInfo> voteInfosMap;


    // @PostConstruct: Spring 프레임워크에서 제공하는 어노테이션 중 하나로, 초기화 작업을 수행하는 메서드를 지정할 때 사용
    @PostConstruct
    private void init() {
        voteInfosMap = new ConcurrentHashMap<Long, VoteInfo>();
    }


    // voteInfoMap에 roomId, 투표 참여자 목록 저장
    public void startVote(Long roomId, int phaseCount, GamePhase phase, Map<Long, GameRole> players) {
        // 무슨 투표이고 어떤 참여자가 투표하는지 HashMap 초기화
        VoteInfo voteInfo = VoteInfo.builder(phaseCount, players);
        voteInfosMap.put(roomId, voteInfo);
        log.info("Room {} VoteInfo {}", roomId, voteInfo.toString());
        voteRedisRepository.startVote(getVoters(roomId), phase);
    }

    // 투표를 하는 메소드
    public Map<Long, Long> vote(Long roomId, Long playerId, Long player) {
        voteRedisRepository.vote(playerId, player);
        return voteResultConvert(getRedisVoteResult(getVoters(roomId)));
    }

    public boolean isEnd(String roomId, int phaseCount) {
        VoteInfo voteInfo = voteInfosMap.get(roomId);
        // roomId에 해당하는 투표 정보가 없거나 해당 차수의 투표가 존재하지 않는다면 끝
        if (voteInfo == null | voteInfo.getPhaseCount() != phaseCount) {
            return true;
        }
        return false;
    }

    // roomId와 phase 넘버가 맞아야 유효한 투표라고 반환하는 메소드
    public boolean isValid(Long playerId, GamePhase phase) {
        return voteRedisRepository.isExist(playerId) == true
                ? (voteRedisRepository.getVote(playerId).getPhase() == phase ? true : false)
                : false;
    }

    public Map<Long, Long> getVoteResult(Long roomId) {
        return voteResultConvert(getRedisVoteResult(getVoters(roomId)));
    }


    // <투표한 사람, 투표 객체>로 반환해 주는 메소드
    public Map<Long, Vote> getRedisVoteResult(List<Long> voters) {
        return voteRedisRepository.getVoteResult(voters);
    }


    // 누가 누구를 투표했는지 Map<투표한사람, 투표받은사람>으로 변환해 주는 메소드
    private Map<Long, Long> voteResultConvert(Map<Long, Vote> voteResult) {
        Map<Long, Long> result = new HashMap<Long, Long>();
        voteResult.forEach((playerId, vote) -> {
            result.put(playerId, vote.getVote());
        });
        return result;
    }

    // roomId에 해당하는 투표에 참여한 참여자 목록 제공
    private List<Long> getVoters(Long roomId) {
        return voteInfosMap.get(roomId) // roomId에 해당하는 VoteInfo
                .getVotersMap().keySet() // VoteInfo에 있는 <String, GameRole> 중 String 값
                .stream().collect(Collectors.toList());
    }
}
