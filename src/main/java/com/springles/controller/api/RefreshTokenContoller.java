package com.springles.controller.api;

import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.response.ResResult;
import com.springles.jwt.JwtTokenUtils;
//import com.springles.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RefreshTokenContoller {

    private final JwtTokenUtils jwtTokenUtils;

    // accessToken 재발급
    @PostMapping("/token/reissue")
    public ResponseEntity<ResResult> reissue(
            @RequestParam("refreshTokenId") String refreshTokenId
    ) {

        ResponseCode responseCode = ResponseCode.ACCESS_TOKEN_REISSUE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data("accessToken : " + jwtTokenUtils.reissue(refreshTokenId))
                        .build());
    }
}
