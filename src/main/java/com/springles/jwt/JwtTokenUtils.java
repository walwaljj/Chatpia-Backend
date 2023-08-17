package com.springles.jwt;

import com.springles.domain.dto.member.MemberLoginRequest;
import com.springles.domain.entity.RefreshToken;
import com.springles.repository.JwtTokenRedisRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenUtils {

    private final Key singleKey;
    private final JwtParser jwtParser;

    public JwtTokenUtils(
            @Value("${jwt.secret}") String jwtSecret,
            JwtTokenRedisRepository tokenRedisRepository
    ) {
        this.singleKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(this.singleKey)
                .build();
    }

    // accessToken 발급
    public String generatedToken(String memberName) {
        Claims jwtClaims = Jwts.claims()
                .setSubject(memberName)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(60 * 60 * 24))); // 1일(test용)
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

    public boolean validate(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("유효기간이 만료된 token");
            return false;
        } catch (MalformedJwtException e) {
            log.warn("유효하지 않은 jwt 서명");
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰");
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 JWT 토큰");
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }
}
