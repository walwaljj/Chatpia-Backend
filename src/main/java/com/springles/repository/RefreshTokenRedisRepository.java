package com.springles.repository;

import com.springles.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByMemberName(String memberName);

    boolean existsByMemberName(String memberName);

    void deleteByMemberName(String memberName);
}
