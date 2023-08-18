package com.springles.controller.api;

import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.dto.member.MemberDeleteRequest;
import com.springles.domain.dto.member.MemberUpdateRequest;
import com.springles.domain.dto.response.ResResult;
import com.springles.service.MemberService;
import com.springles.service.impl.MemberServiceImpl;
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

    /* 로그인 구현 후 memberId 인증 -> 헤더 인증으로 변경 예정 */
    @PatchMapping("/info/{memberId}")
    public ResponseEntity<ResResult> updateInfo(
            @Valid @RequestBody MemberUpdateRequest memberDto,
            @PathVariable("memberId") Long memberId
    ) {
        ResponseCode responseCode = ResponseCode.MEMBER_UPDATE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.updateInfo(memberDto, memberId))
                        .build());
    }

    /* 로그인 구현 후 memberId 인증 -> 헤더 인증으로 변경 예정 */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ResResult> signOut(
            @Valid @RequestBody MemberDeleteRequest memberDto,
            @PathVariable("memberId") Long memberId
    ) {
        memberService.signOut(memberDto, memberId);
        ResponseCode responseCode = ResponseCode.MEMBER_DELETE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .build());
    }
}
