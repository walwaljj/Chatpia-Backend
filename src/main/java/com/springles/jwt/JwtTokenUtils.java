package com.springles.jwt;

import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtils {

    private final Key singleKey;
    private final JwtParser jwtParser;

    public JwtTokenUtils(
            @Value("${jwt.secret}") String jwtSecret
    ) {
        this.singleKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(this.singleKey)
                .build();
    }

    public String generatedToken(UserDetails userDetails) {
        Claims jwtClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(60 * 60 * 24)));
        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(singleKey)
                .compact();
    }

    public boolean validate(String token) {
        try {
            jwtParser.parseClaimsJwt(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT validation failed");
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return jwtParser.parseClaimsJwt(token).getBody();
    }
}
