package com.springles.service.impl;

import com.springles.domain.dto.cookie.CookieSetRequest;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.service.CookieService;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CookieServiceImpl implements CookieService {

    // accessToken 쿠키 설정
    public void setAtkCookie(CookieSetRequest cookieDto, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieDto.getKey(), cookieDto.getValue());
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 1시간(테스트용)
        cookie.setHttpOnly(true);
        cookie.setSecure(false);   // 사파리 브라우저에서 쿠키 저장이 안되는 이슈 해결을 위해 설정 해제
        response.addCookie(cookie);
        log.info(cookieDto.getKey() + ":" + cookieDto.getValue());
    }


    // refreshToken 쿠키 설정
    public void setRtkCookie(CookieSetRequest cookieDto, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieDto.getKey(), cookieDto.getValue());
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);    // 2주
        cookie.setHttpOnly(true);
        cookie.setSecure(false);   // 사파리 브라우저에서 쿠키 저장이 안되는 이슈 해결을 위해 설정 해제
        response.addCookie(cookie);
        log.info(cookieDto.getKey() + ":" + cookieDto.getValue());
    }

    // Token 쿠키 초기화 설정
    public void setInitCookie(CookieSetRequest cookieDto, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieDto.getKey(), cookieDto.getValue());
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);   // 사파리 브라우저에서 쿠키 저장이 안되는 이슈 해결을 위해 설정 해제
        response.addCookie(cookie);
    }

    // 쿠키에서 accessToken 호출
    public String atkFromCookie(HttpServletRequest request) {
        String accessToken = "";
        Cookie[] cookies = request.getCookies();

        if(cookies.length != 0) {
            log.info("utils: 쿠키 있음");
            for (Cookie cookie : cookies) {
                // 쿠키에 accessToken이 있는지 확인
                if ("accessToken".equals(cookie.getName())) {
                    log.info("utils: accessToken 있음");
                    accessToken = cookie.getValue();
                    log.info("utils: " + accessToken);
                    break;
                }
            }
            // 쿠키에 accessToken이 없으면 request.attribute에서 추출
            if(accessToken.equals("")) {
                accessToken = String.valueOf(request.getAttribute("accessToken"));
            }
        } else {
            throw new CustomException(ErrorCode.NOT_AUTHORIZED_CONTENT);
        }
        return accessToken;
    }
}


