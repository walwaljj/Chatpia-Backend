package com.springles.repository;

import com.springles.domain.entity.GameSession;
import org.springframework.data.repository.CrudRepository;

public interface GameSessionRedisRepository extends CrudRepository<GameSession, Long> {

    GameSession findByHostId(Long playerId);
}
