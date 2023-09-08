package com.springles.controller.api;


import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.cookie.CookieSetRequest;
import com.springles.domain.dto.response.ResResult;
import com.springles.service.CookieService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("cookie")
public class CookieController {

    private final CookieService cookieService;

    // AccessToken 생성
    @PostMapping("/atk")
    public ResponseEntity<ResResult> setAtkCookie(
            @RequestBody CookieSetRequest cookieDto,
            HttpServletResponse response
    ) {
        ResponseCode responseCode = ResponseCode.COOKIE_SET;
        cookieService.setAtkCookie(cookieDto, response);

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .build()
        );
    }

    // RefreshToken 생성
    @PostMapping("/rtk")
    public ResponseEntity<ResResult> setRtkCookie(
            @RequestBody CookieSetRequest cookieDto,
            HttpServletResponse response
    ) {
        ResponseCode responseCode = ResponseCode.COOKIE_SET;
        cookieService.setRtkCookie(cookieDto, response);

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .build());
    }
}
