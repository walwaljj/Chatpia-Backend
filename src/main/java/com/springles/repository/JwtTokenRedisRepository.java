package com.springles.repository;

import com.springles.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JwtTokenRedisRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByMemberName(String memberName);
}
