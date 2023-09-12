package com.springles.jwt;

import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            log.info("redirect error 페이지");
            response.sendRedirect("/v1/login-page?error");

            // 로그아웃된 토큰일 경우, error 파라미터 없이 redirect
            if(e.getErrorCode().equals(ErrorCode.LOGOUT_TOKEN)) {
                response.sendRedirect("/v1/login-page");
            }
        }
    }
}
