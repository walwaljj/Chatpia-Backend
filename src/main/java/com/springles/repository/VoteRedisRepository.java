package com.springles.repository;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
@AllArgsConstructor
public class VoteRedisRepository {
    // RedisTemplate: Redis 데이터베이스와 상호작용하기 위한 편리한 방법을 제공하는 도구
    private final RedisTemplate<String, Vote> redisTemplate;
    // HashOperations: Redis 해시에 데이터를 추가하고 조회하고 수정하고 삭제하는 작업을 간편하게 수행
    private HashOperations<String, String, Vote> opsHashVote;
    private static final String key = "Vote";

    public Map<String, Vote> startVote(List<String> players, GamePhase phase) {
        Map<String, Vote> voteResult = new HashMap<String, Vote>();
        players.forEach((playerId) -> {
            Vote vote;
            if(!isExist(playerId)) {
                // 존재하지 않으면 새로 생성
                vote = Vote.builder(playerId, phase);
            } else {
                // 존재하면 가져오기
                vote = getVote(playerId);
            }
            updateVote(playerId, vote);
        });
        return voteResult;
    }

    // playerId에 해당하는 사용자가 남긴 투표가 존재하는지 검색
    public boolean isExist(String playerId) {
        return opsHashVote.hasKey(key, playerId);
    }

    // playerId에 해당하는 사용자가 남긴 투표 반환
    public Vote getVote(String playerId) {
        return opsHashVote.get(key, playerId);
    }

    // Vote를 받아서 업데이트하는 함수
    private void updateVote(String playerId, Vote voteDao) {
        opsHashVote.put(key, playerId, voteDao);
    }

    private void deleteVote(String playerId) {
        opsHashVote.delete(key, playerId);
    }
}
