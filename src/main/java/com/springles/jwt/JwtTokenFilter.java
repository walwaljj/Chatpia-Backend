package com.springles.jwt;

import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
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
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 헤더 정보 유효성 체크
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.split(" ")[1];
            // 로그아웃 여부 체크
            if (jwtTokenUtils.isNotLogout(accessToken)) {
                // accessToken 유효성 체크
                if (jwtTokenUtils.validate(accessToken)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    String memberName = jwtTokenUtils.parseClaims(accessToken).getSubject();

                    AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            MemberCreateRequest.builder()
                                    .memberName(memberName)
                                    .build(),
                            accessToken, new ArrayList<>()
                    );
                    context.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(context);
                }
            } else {
                throw new CustomException(ErrorCode.NO_JWT_TOKEN);
            }
        }
        filterChain.doFilter(request, response);
    }
}
