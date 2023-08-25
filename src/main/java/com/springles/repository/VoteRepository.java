package com.springles.repository;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.entity.VoteInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
    private ConcurrentHashMap<String, VoteInfo> voteInfosMap;


    // @PostConstruct: Spring 프레임워크에서 제공하는 어노테이션 중 하나로, 초기화 작업을 수행하는 메서드를 지정할 때 사용
    @PostConstruct
    private void init() {
        voteInfosMap = new ConcurrentHashMap<String, VoteInfo>();
    }

    public void startVote(String roomId, int phaseCount, GamePhase phase, Map<String, GameRole> players) {
        // 무슨 투표이고 어떤 참여자가 투표하는지 HashMap 초기화
        VoteInfo voteInfo = VoteInfo.builder(phaseCount, players);
        voteInfosMap.put(roomId, voteInfo);
        log.info("Room {} VoteInfo {}", roomId, voteInfo.toString());
        voteRedisRepository.startVote(getVoters(roomId), phase);
    }

    private List<String> getVoters(String roomId) {
        return voteInfosMap.get(roomId) // roomId에 해당하는 VoteInfo
                .getVotersMap().keySet() // VoteInfo에 있는 <String, GameRole> 중 String 값
                .stream().collect(Collectors.toList());
    }
}
