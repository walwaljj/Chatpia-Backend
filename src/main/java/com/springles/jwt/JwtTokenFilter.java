package com.springles.jwt;

import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
    private String accessToken = "";
    private String refreshTokenId = "";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            log.info("쿠키 != null");

            for (Cookie cookie : cookies) {
                // 쿠키에서 accessToken 추출
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    request.setAttribute("accessToken", accessToken);
                    log.info("uri : " + request.getRequestURI());
                    log.info("accessToken : " + accessToken);
                }
            }
            // 로그아웃 여부 체크
            if (jwtTokenUtils.isNotLogout(accessToken)) {
                /* accessToken 유효성 체크
                * 0 : 유효하지 않은 JWT 서명, 지원되지 않는 JWT토큰, 잘못된 JWT 토큰
                * 1 : 유효한 토큰
                * 2 : 유효기간이 만료된 토큰
                * */
                if (jwtTokenUtils.validate(accessToken) != 0) {
                    // accessToken의 유효기간 만료 시 refreshToken으로 재발급
                    if (jwtTokenUtils.validate(accessToken) == 2) {
                        for (Cookie cookie : cookies) {
                            // 쿠키에서 refreshTokenId 추출
                            if ("refresehTokenId".equals(cookie.getName())) {
                                refreshTokenId = cookie.getValue();
                                request.setAttribute("refreshTokenId", refreshTokenId);
                                log.info("uri : " + request.getRequestURI());
                                log.info("refreshTokenId : " + refreshTokenId);
                                // accessToken 갱신
                                accessToken = jwtTokenUtils.reissue(refreshTokenId);
                                request.setAttribute("accessToken", accessToken);
                                log.info("newAccessToken : " + accessToken);
                            }
                            // refreshToken이 없을 경우
                            throw new CustomException(ErrorCode.NO_JWT_TOKEN);
                        }
                    }
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
