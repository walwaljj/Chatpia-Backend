package com.springles.service;

import com.springles.domain.dto.cookie.CookieSetRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {

    // accessToken 쿠키 설정
    void setAtkCookie(CookieSetRequest cookieDto, HttpServletResponse response);

    // refreshToken 쿠키 설정
    void setRtkCookie(CookieSetRequest cookieDto, HttpServletResponse response);

    // 쿠키 초기화
    void setInitCookie(CookieSetRequest cookieDto, HttpServletResponse response);

    // 쿠키에서 accessToken 추출
    String atkFromCookie(HttpServletRequest request);
}
