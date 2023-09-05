package com.springles.jwt;

import com.springles.domain.entity.RefreshToken;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.BlackListTokenRedisRepository;
import com.springles.repository.MemberJpaRepository;
import com.springles.repository.RefreshTokenRedisRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenUtils {

    private final Key singleKey;
    private final JwtParser jwtParser;
    private final BlackListTokenRedisRepository blackListTokenRedisRepository;

    private final MemberJpaRepository memberJpaRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public JwtTokenUtils(
            @Value("${jwt.secret}") String jwtSecret,
            BlackListTokenRedisRepository blackListTokenRedisRepository,
            MemberJpaRepository memberJpaRepository,
            RefreshTokenRedisRepository refreshTokenRedisRepository
    ) {
        this.singleKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(this.singleKey)
                .build();
        this.blackListTokenRedisRepository = blackListTokenRedisRepository;
        this.memberJpaRepository = memberJpaRepository;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
    }

    // accessToken 발급
    public String generatedToken(String memberName) {
        Claims jwtClaims = Jwts.claims()
                .setSubject(memberName)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(60 * 60))); // 테스트용 1시간
        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(singleKey)
                .compact();
    }

    // refreshToken 발급
    public RefreshToken generaedRefreshToken(String memberName) {
        return RefreshToken.builder()
                .memberName(memberName)
                .refreshToken(String.valueOf(UUID.randomUUID()))
                .expiration(60 * 60 * 24 * 14L)   // 2주
                .build();
    }

    // accessToken 재발급
    public String reissue(String refreshTokenId) {

        // refreshToken이 있는지 확인
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRedisRepository.findById(refreshTokenId);
        if(optionalRefreshToken.isEmpty()) {
            log.warn("refresh token이 DB에 존재하지 않음");
//            throw new CustomException(ErrorCode.NO_JWT_TOKEN);
            return "";
        }

        // refreshToken이 유효한지 확인
        if(!memberJpaRepository.existsByMemberName(optionalRefreshToken.get().getMemberName())) {
            log.warn("refresh token이 유효하지 않음");
//            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
            return "";
        }

        // accessToken 재발급
        return generatedToken(optionalRefreshToken.get().getMemberName());
    }

//    public boolean validate(String accessToken) {
//        try {
//            jwtParser.parseClaimsJws(accessToken);
//            return true;
//        } catch (ExpiredJwtException e) {
//            log.warn("유효기간이 만료된 token");
//            return false;
//        } catch (MalformedJwtException e) {
//            log.warn("유효하지 않은 jwt 서명");
//            return false;
//        } catch (UnsupportedJwtException e) {
//            log.warn("지원되지 않는 JWT 토큰");
//            return false;
//        } catch (IllegalArgumentException e) {
//            log.warn("잘못된 JWT 토큰");
//            return false;
//        }
//    }

    public int validate(String accessToken) {
        try {
            jwtParser.parseClaimsJws(accessToken).getBody();
            //log.info("유효한 토큰");
            return 1;
        } catch (ExpiredJwtException e) {
            log.warn("유효기간이 만료된 token");
            return 2;
        } catch (MalformedJwtException e) {
            log.warn("유효하지 않은 jwt 서명");
            return 0;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰");
            return 0;
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 JWT 토큰");
            return 0;
        }
    }

    public Claims parseClaims(String accessToken) {
        return jwtParser.parseClaimsJws(accessToken).getBody();
    }

    public boolean isNotLogout(String accessToken) {
        if(!blackListTokenRedisRepository.existsByAccessToken(accessToken)) {
            return true;
        } else {
            log.warn("로그아웃 된 토큰");
            return false;
        }
    }

    // accessToken 쿠키 설정
    public void setAtkCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 테스트용 1시간
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
