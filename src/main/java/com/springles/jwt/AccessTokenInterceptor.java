//package com.springles.jwt;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//public class AccessTokenInterceptor implements HandlerInterceptor {
//
//    // 쿠키에서 accessToken 을 가져옴
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        Cookie[] cookies = request.getCookies();
//        String accessToken = "";
//        for (Cookie cookie : cookies) {
//            if ("accessToken".equals(cookie.getName())) {
//                accessToken = cookie.getValue();
//                break;
//            }
//        }
//
//        request.setAttribute("accessToken", accessToken);
//        return true;
//    }
//}
