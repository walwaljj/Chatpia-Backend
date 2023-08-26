package com.springles.repository;

import com.springles.domain.entity.GameSession;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface GameSessionRedisRepository extends CrudRepository<GameSession, Long> {

    Optional<GameSession> findByHostId(Long playerId);
}
