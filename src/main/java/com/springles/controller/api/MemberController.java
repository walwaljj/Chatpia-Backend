package com.springles.controller.api;

import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.member.*;
import com.springles.domain.dto.response.ResResult;
import com.springles.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ResResult> signup(
            @Valid @RequestBody MemberCreateRequest memberDto
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_SAVE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.signUp(memberDto))
                        .build());
    }

    @PatchMapping("/info")
    public ResponseEntity<ResResult> updateInfo(
            @Valid @RequestBody MemberUpdateRequest memberDto,
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_UPDATE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.updateInfo(memberDto, authHeader))
                        .build());
    }

    @DeleteMapping
    public ResponseEntity<ResResult> signOut(
            @Valid @RequestBody MemberDeleteRequest memberDto,
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        memberService.signOut(memberDto, authHeader);
        ResponseCode responseCode = ResponseCode.MEMBER_DELETE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResResult> login(
            @Valid @RequestBody MemberLoginRequest memberDto
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_LOGIN;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.login(memberDto))
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ResResult> logout(
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_LOGOUT;
        memberService.logout(authHeader);
        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .build()
        );
    }

    @PostMapping("/vertification/id")
    public ResponseEntity<ResResult> vertificationId(
            @RequestBody MemberVertifIdRequest memberDto
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_ID_SEND;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.vertificationId(memberDto))
                        .build()
        );
    }

    @PostMapping("/vertification/pw")
    public ResponseEntity<ResResult> vertificationPw(
            @RequestBody MemberVertifPwRequest memberDto
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_PW_SEND;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.vertificationPw(memberDto))
                        .build()
        );
    }
}
