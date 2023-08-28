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

    @PostMapping("/info/profile")
    public ResponseEntity<ResResult> createProfile(
            @Valid @RequestBody MemberProfileCreateRequest memberDto,
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_PROFILE_CREATE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.createProfile(memberDto, authHeader))
                        .build()
        );
    }

    @PatchMapping("/info/profile")
    public ResponseEntity<ResResult> updateProfile(
            @Valid @RequestBody MemberProfileUpdateRequest memberDto,
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_PROFILE_UPDATE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.updateProfile(memberDto, authHeader))
                        .build()
        );
    }

    @GetMapping("/info/profile")
    public ResponseEntity<ResResult> readProfile(
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_PROFILE_READ;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.readProfile(authHeader))
                        .build()
        );
    }

    @PatchMapping("/levelup")
    public ResponseEntity<ResResult> levelUp(
            // 테스트를 위해 param으로 받음 -> 추후 @RequestParam 삭제 필요
            @RequestParam("memberId") Long memberId
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_LEVEL_UP;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.levelUp(memberId))
                        .build()
        );
    }

    @GetMapping("/record")
    public ResponseEntity<ResResult> readRecord(
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_GAME_RECORD_READ;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.readRecord(authHeader))
                        .build()
        );
    }

    @PatchMapping("/record")
    public ResponseEntity<ResResult> updateRecord(
            @RequestParam("memberId") Long memberId
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_GAME_RECORD_UPDATE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.updateRecord(memberId))
                        .build()
        );
    }
}
