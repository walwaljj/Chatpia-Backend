package com.springles.jwt;

import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.repository.BlackListTokenRedisRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("필터 시작");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.split(" ")[1];
            log.info("필터 - 로그아웃 여부 체크");
            if (jwtTokenUtils.isNotLogout(token)) {
                log.info("필터 - 토큰 유효성 검사");
                if (jwtTokenUtils.validate(token)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    String memberName = jwtTokenUtils.parseClaims(token).getSubject();

                    AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            MemberCreateRequest.builder()
                                    .memberName(memberName)
                                    .build(),
                            token, new ArrayList<>()
                    );
                    context.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(context);
                }
            }
        }
        log.info("필터 끝");
        filterChain.doFilter(request, response);
    }
}
