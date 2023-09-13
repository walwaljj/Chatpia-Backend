package com.springles.jwt;

import com.springles.domain.dto.cookie.CookieSetRequest;
import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.service.CookieService;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final CookieService cookieService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = "";
        String refreshTokenId = "";

        Cookie[] cookies = request.getCookies();

        log.info(request.getRequestURI());

        /**
         *  쿠키에서 accessToken(atk), refreshToken(rtk) 추출
         *  */
        if (cookies != null) {
            log.info("쿠키 있음");

            for (Cookie cookie : cookies) {

                // 쿠키에 atk가 있는지 확인
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    log.info("쿠키에 atk 있음");
                    log.info("atk: " + accessToken);
                    // 혹은 쿠키에 rtk가 있는지 확인
                } else if ("refreshTokenId".equals(cookie.getName())) {
                    refreshTokenId = cookie.getValue();
                    log.info("쿠키에 rtk 있음");
                    log.info("rtk: " + refreshTokenId);
                }
            }
        } else {
            /*
             * [쿠키가 null이면 인증 절차를 거치지 않음]
             * 권한이 필요한 api일 경우, 로그인 화면으로 이동
             * 권한이 필요 없는 api일 경우, 화면 정상 출력
             */
        }


        /**
         *  토큰 존재유무/유효성 판별
         *  */
        // 쿠키에 atk가 있는지 확인
        if (!accessToken.equals("")) {

            // atk가 로그아웃 됐는지 확인
            if (jwtTokenUtils.isNotLogout(accessToken)) {
                log.info("로그아웃 안됨");

                // atk가 정상인지 확인(서명 등)
                if (jwtTokenUtils.validate(accessToken) == 1) {
                    log.info("atk valid 정상");

                    // atk의 유효시간이 적게 남았는지 확인
                    if(((jwtTokenUtils.parseClaims(accessToken).getExpiration().getTime() - Date.from(Instant.now()).getTime()) / 1000) < 60 * 5L) {
                        // rtk가 추출되었는지 확인
                        if (!refreshTokenId.equals("")) {

                            // atk 재발급
                            accessToken = jwtTokenUtils.reissue(refreshTokenId);

                            // atk 값이 비어있지 않은지 확인
                            // 재발급을 했는데도 불구하고 값이 비어있으면 rtk가 비정상인 것
                            if (!accessToken.equals("")) {
                                // atk가 정상
                                log.info("atk 재발급 완료");

                                // 쿠키에 저장
                                cookieService.setAtkCookie(
                                        CookieSetRequest.builder().key("accessToken").value(accessToken).build(), response);
                                log.info("atk 쿠키에 저장 완료");

                                // attribute에 저장
                                // atk 재발급 후 쿠키에 저장해도 request 속 쿠키값은 변하지 않아 유효하지 않은 토큰으로 필터를 지나치게됨
                                // 이를 해결하기 위해 재발급 시에만 request.setAttibute에도 atk 저장
                                request.setAttribute("accessToken", accessToken);

                            } else {
                                // atk 값이 비어있을 경우
                                log.info("rtk 비정상, 재발급 불가");

                                // 현재 rtk 쿠키 초기화
                                refreshTokenId = "";
                                accessToken = "";

                                // rtk 쿠키 삭제
                                cookieService.setInitCookie(
                                        CookieSetRequest.builder().key("refreshTokenId").value(null).build(), response);

                                // 예외 처리(로그인 화면으로 이동)
                                throw new CustomException(ErrorCode.NO_JWT_TOKEN);
                            }
                        } else {
                            /*
                             * rtk가 없다면 atk 재발급하지 않고 인증 진행
                             */
                        }
                    }

                    // 인증객체 생성
                    log.info("인증객체 생성");
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    String memberName = jwtTokenUtils.parseClaims(accessToken).getSubject();
                    log.info("memberName : " + memberName );

                    AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            MemberCreateRequest.builder()
                                    .memberName(memberName)
                                    .build(),
                            accessToken, new ArrayList<>()
                    );
                    context.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(context);
                }
                // atk가 정상이 아닐 경우
                else {
                    // 현재 atk 초기화
                    accessToken = "";
                    log.info("atk valid 비정상");
                    log.info("atk : " + accessToken);

                    // atk 쿠키 삭제
                    cookieService.setInitCookie(
                            CookieSetRequest.builder().key("accessToken").value(null).build(), response);

                    // atk의 유효기간이 만료된 거였다면 rtk 확인
                    if (jwtTokenUtils.validate(accessToken) == 2) {
                        log.info("atk valid 비정상 - 유효기간 만료");

                        // rtk가 추출되었는지 확인
                        if (!refreshTokenId.equals("")) {

                            // atk 재발급
                            accessToken = jwtTokenUtils.reissue(refreshTokenId);

                            // atk 값이 비어있지 않은지 확인
                            // 재발급을 했는데도 불구하고 값이 비어있으면 rtk가 비정상인 것
                            if (!accessToken.equals("")) {
                                // atk가 정상
                                log.info("atk 재발급 완료");

                                // 쿠키에 저장
                                cookieService.setAtkCookie(
                                        CookieSetRequest.builder().key("accessToken").value(accessToken).build(), response);

                                // attribute에 저장
                                // atk 재발급 후 쿠키에 저장해도 request 속 쿠키값은 변하지 않아 유효하지 않은 토큰으로 필터를 지나치게됨
                                // 이를 해결하기 위해 재발급 시에만 request.setAttibute에도 atk 저장
                                request.setAttribute("accessToken", accessToken);

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

                            } else {
                                // atk 값이 비어있을 경우
                                log.info("rtk 비정상, 재발급 불가");

                                // 현재 rtk 쿠키 초기화
                                refreshTokenId = "";

                                // rtk 쿠키 삭제
                                cookieService.setInitCookie(
                                        CookieSetRequest.builder().key("refreshTokenId").value(null).build(), response);

                                // 예외 처리(로그인 화면으로 이동)
                                throw new CustomException(ErrorCode.NO_JWT_TOKEN);
                            }
                        } else {
                            // rtk가 추출되지 않았다면 예외 처리(로그인 화면으로 이동)
                            log.info("쿠키에 rtk 없음");
                            throw new CustomException(ErrorCode.NO_JWT_TOKEN);
                        }

                    } else {
                        // atk가 비정상적인 형태라면 예외 처리(로그인 화면으로 이동)
                        log.info("atk 비정상 - 서명 등");
                        throw new CustomException(ErrorCode.NO_JWT_TOKEN);
                    }
                }
            } else {
                // atk가 로그아웃된 토큰일 경우
                log.info("로그아웃 됨");

                // 현재 atk, rtk 초기화
                accessToken = "";
                refreshTokenId = "";

                // atk, rtk 쿠키 삭제
                cookieService.setInitCookie(
                        CookieSetRequest.builder().key("accessToken").value(null).build(), response);
                cookieService.setInitCookie(
                        CookieSetRequest.builder().key("refreshTokenId").value(null).build(), response);

                // 예외 처리(로그인 화면으로 이동)
                throw new CustomException(ErrorCode.LOGOUT_TOKEN);
            }
            // atk가 없고 rtk만 있을 경우
        } else if (!refreshTokenId.equals("")) {
            log.info("atk 없고 rtk만 있음");

            // atk 재발급
            accessToken = jwtTokenUtils.reissue(refreshTokenId);

            // atk 값이 비어있지 않은지 확인
            // 재발급을 했는데도 불구하고 값이 비어있으면 rtk가 비정상인 것
            if (!accessToken.equals("")) {
                // atk가 정상
                log.info("atk 재발급 완료");

                // 쿠키에 저장
                cookieService.setAtkCookie(
                        CookieSetRequest.builder().key("accessToken").value(accessToken).build(), response);

                // attribute에 저장
                // atk 재발급 후 쿠키에 저장해도 request 속 쿠키값은 변하지 않아 유효하지 않은 토큰으로 필터를 지나치게됨
                // 이를 해결하기 위해 재발급 시에만 request.setAttibute에도 atk 저장
                request.setAttribute("accessToken", accessToken);

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

            } else {
                // atk 값이 비어있을 경우
                log.info("rtk 비정상, 재발급 불가");

                // 현재 rtk 쿠키 초기화
                refreshTokenId = "";

                // rtk 쿠키 삭제
                cookieService.setInitCookie(
                        CookieSetRequest.builder().key("refreshTokenId").value(null).build(), response);

                // 예외 처리(로그인 화면으로 이동)
                throw new CustomException(ErrorCode.NO_JWT_TOKEN);
            }
        } else {
            /*
             * [쿠키에서 atk, rtk 모두 추출되지 않았다면 인증 절차를 거치지 않음]
             * 권한이 필요한 api일 경우, 로그인 화면으로 이동
             * 권한이 필요 없는 api일 경우, 화면 정상 출력
             */
            log.info("쿠키에 atk, rtk 모두 없음");
        }
        log.info("필터 끝");
        filterChain.doFilter(request, response);
    }
}
