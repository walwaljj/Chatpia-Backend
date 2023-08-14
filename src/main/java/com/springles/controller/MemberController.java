package com.springles.controller;

import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.member.MemberDetails;
import com.springles.domain.dto.response.ResResult;
import com.springles.service.impl.MemberDetailsManagerImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberDetailsManagerImpl userDetailsManager;

    @PostMapping("/signup")
    public ResponseEntity<ResResult> signup(
            @Valid @RequestBody MemberDetails memberDetails
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_SAVE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(userDetailsManager.signUp(memberDetails))
                        .build());
    }

    /* 로그인 구현 후 memberId 인증 -> 헤더 인증으로 변경 예정 */
    @PatchMapping("/info/{memberId}")
    public ResponseEntity<ResResult> updateInfo(
            @Valid @RequestBody MemberDetails memberDetails,
            @PathVariable("memberId") Long memberId
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_UPDATE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(userDetailsManager.updateInfo(memberDetails, memberId))
                        .build());
    }

    /* 로그인 구현 후 memberId 인증 -> 헤더 인증으로 변경 예정 */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ResResult> signOut(
            @RequestBody MemberDetails memberDetail,
            @PathVariable("memberId") Long memberId
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_DELETE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(userDetailsManager.signOut(memberDetail, memberId))
                        .build());
    }
}
