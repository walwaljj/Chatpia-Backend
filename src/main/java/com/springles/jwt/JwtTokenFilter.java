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

        /** 쿠키에서 accessToken과 refreshTokenId 추출 */
        if (cookies != null) {
            //log.info("쿠키 != null");

            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    request.setAttribute("accessToken", accessToken);
                }
                if ("refreshTokenId".equals(cookie.getName())) {
                    refreshTokenId = cookie.getValue();
                }
            }

            /**  쿠키에 accessToken이 존재할 경우 */
            if (!accessToken.equals("")) {
                //log.info("쿠키에 accessToken이 존재");

                // accessToken 로그아웃 여부 체크
                if (jwtTokenUtils.isNotLogout(accessToken)) {
                    //log.info("로그아웃 안됨");

                    /* accessToken 유효성 체크
                     * 0 : 유효하지 않은 JWT 서명, 지원되지 않는 JWT토큰, 잘못된 JWT 토큰
                     * 1 : 유효한 토큰
                     * 2 : 유효기간이 만료된 토큰
                     * */
                    if (jwtTokenUtils.validate(accessToken) != 0) {
                        //log.info("validate(accessToken) != 0");

                        // accessToken 유효기간 만료 or 짧게 남음 체크
                        if ((jwtTokenUtils.validate(accessToken) == 2)
//                                || ((jwtTokenUtils.parseClaims(accessToken).getExpiration().getTime() - Date.from(Instant.now()).getTime()) / 1000) < 30L
                        ) {
                            log.info("유효기간 만료 or 적게남음");

                            // refreshTokenId가 있을 경우
                            if (!refreshTokenId.equals("")) {
                                // accessToken 갱신
                                // 만약 refreshToken이 DB에 존재하거나 유효하지 않을 경우 jwtTokenUtils.reissue() 메소드에서 ""가 반환됨(유효x)
                                accessToken = jwtTokenUtils.reissue(refreshTokenId);

                                // 갱신한 accessToken이 유효한지 체크
                                if (!accessToken.equals("")) {
                                    // accessToken 재발급 후 쿠키, attribute에 저장
                                    jwtTokenUtils.setAtkCookie("accessToken", accessToken, response);
                                    request.setAttribute("accessToken", accessToken);
                                    log.info("accessToken 재발급 완료");
                                }

                                // refreshToken이 DB 상에 존재하지 않거나 유효하지 않는 경우
                                // (쿠키에는 있었으나 로직을 수행하는 사이 refreshToken의 유효시간이 지나서 DB에서 삭제된 경우)
                                else {
                                    log.info("refreshToken이 DB에 존재하지 않거나 유효하지 않음");
                                    throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
                                }
                            }

                            // 쿠키에 refreshTokenId가 없을 경우
                            else {
                                log.info("refreshToken이 쿠키에 존재하지 않음");
                                throw new CustomException(ErrorCode.NO_JWT_TOKEN);
                            }
                        }
                    }
                    // accessToken이 유효하지 않을 경우
                    else {
                        log.info("accessToken이 유효하지 않음");
                        throw new CustomException(ErrorCode.NO_JWT_TOKEN);
                    }


                    // 인증객체 생성
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
            }

            /** accessToken이 존재하지 않고, refreshTokenId만 존재할 경우 */
            else if(!refreshTokenId.equals("")) {
                accessToken = jwtTokenUtils.reissue(refreshTokenId);

                // 갱신한 accessToken이 유효한지 체크
                if (!accessToken.equals("")) {
                    // accessToken 재발급 후 쿠키, attribute에 저장
                    jwtTokenUtils.setAtkCookie("accessToken", accessToken, response);
                    request.setAttribute("accessToken", accessToken);
                    log.info("accessToken 재발급 완료");

                    // 인증객체 생성
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

                // refreshToken이 DB 상에 존재하지 않거나 유효하지 않는 경우 refreshToken 초기화
                // (쿠키에는 있었으나 로직을 수행하는 사이 refreshToken의 유효시간이 지나서 DB에서 삭제된 경우)
                else {
                    refreshTokenId = "";
                    log.info("refreshToken이 DB에 존재하지 않거나 유효하지 않음");
                    throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
