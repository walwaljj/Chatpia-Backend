package com.springles.service.impl;

import com.springles.domain.entity.RefreshToken;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.jwt.JwtTokenUtils;
import com.springles.repository.MemberJpaRepository;
import com.springles.repository.RefreshTokenRedisRepository;
import com.springles.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public String reissue(String refreshTokenId) {

        // refreshToken이 있는지 확인
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRedisRepository.findById(refreshTokenId);
        if(optionalRefreshToken.isEmpty()) {
            log.warn("refresh token이 존재하지 않음");
            throw new CustomException(ErrorCode.NO_JWT_TOKEN);
        }

        // refreshToken이 유효한지 확인
        if(!memberJpaRepository.existsByMemberName(optionalRefreshToken.get().getMemberName())) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // accessToken 재발급
        return jwtTokenUtils.generatedToken(optionalRefreshToken.get().getMemberName());
    }
}
