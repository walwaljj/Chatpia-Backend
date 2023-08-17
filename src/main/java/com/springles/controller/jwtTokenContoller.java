package com.springles.controller;

import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.response.ResResult;
import com.springles.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class jwtTokenContoller {

    private final JwtTokenService jwtTokenService;

    // accessToken 재발급
    @PostMapping("/token/reissue")
    public ResponseEntity<ResResult> reissue(
            @RequestParam String refreshTokenId
    ) {

        ResponseCode responseCode = ResponseCode.ACCESS_TOKEN_REISSUE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(jwtTokenService.reissue(refreshTokenId))
                        .build());
    }
}
