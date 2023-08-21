package com.springles.repository;

import com.springles.domain.entity.BlackListToken;
import org.springframework.data.repository.CrudRepository;

public interface BlackListTokenRedisRepository extends CrudRepository<BlackListToken, String> {
    boolean existsByAccessToken(String accessToken);
}
