package com.springles.repository;

import com.springles.domain.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RefreshTokenRedisRepositoryTest {

    @Autowired
    RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Test
    @DisplayName("memberName에 해당하는 RefreshToken 호출 테스트")
    void findByMemberName() {
        // given
        refreshTokenRedisRepository.save(
                RefreshToken.builder()
                        .memberName("user1")
                        .refreshToken("flvmfptlxhzmsxptmxm")
                        .expiration(60 * 60 * 5L)
                        .build()
        );

        // when
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRedisRepository.findByMemberName("user1");

        // then
        assertNotNull(optionalRefreshToken.get().getId());
        assertEquals(optionalRefreshToken.get().getMemberName(), "user1");
        assertEquals(optionalRefreshToken.get().getRefreshToken(), "flvmfptlxhzmsxptmxm");
        assertEquals(optionalRefreshToken.get().getExpiration(), 60 * 60 * 5L);
    }
}