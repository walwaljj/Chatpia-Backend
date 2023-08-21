package com.springles.repository;

import com.springles.domain.entity.BlackListToken;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BlackListTokenRedisRepositoryTest {

    @Autowired
    BlackListTokenRedisRepository blackListTokenRedisRepository;

    @Test
    void existsByAccessToken() {
        // given
        blackListTokenRedisRepository.save(
                BlackListToken.builder()
                        .accessToken("qmfforfltmxmxhzmsxptmxm")
                        .expiration(60 * 60 * 5L)
                        .build()
        );

        // when
        boolean result = blackListTokenRedisRepository.existsByAccessToken("qmfforfltmxmxhzmsxptmxm");

        // then
        assertTrue(result);
    }
}