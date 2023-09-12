package com.springles.controller.api;

import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.member.*;
import com.springles.domain.dto.response.ResResult;
import com.springles.jwt.JwtTokenUtils;
import com.springles.service.CookieService;
import com.springles.service.MemberService;
import com.springles.valid.ValidationSequence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final CookieService cookieService;

    // 채팅 참여자 목록 정보 조회
    @GetMapping("/info/player")
    public ResponseEntity<ResResult> playerInfo(@RequestBody PlayerInfoRequest playerInfoRequest) {
        // 멤버 정보 가져오기
        ResponseCode responseCode = ResponseCode.MEMBER_DETAIL;
        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.getPlayerInfo(playerInfoRequest))
                        .build());
    }


    // 멤버 프로필 조회(simple)
    @GetMapping("/info/profile/simple")
    public ResponseEntity<ResResult> profileInfo(HttpServletRequest request) {
        // 멤버 정보 가져오기
        String accessToken = cookieService.atkFromCookie(request);
        ResponseCode responseCode = ResponseCode.MEMBER_DETAIL;
        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.getUserSimpleProfileInfo(accessToken))
                        .build());
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResResult> signup(
            @Validated({ValidationSequence.class}) @RequestBody MemberCreateRequest memberDto
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


    // 회원 정보 관리(수정)
    @PatchMapping("/info")
    public ResponseEntity<ResResult> updateInfo(
            @Validated({ValidationSequence.class}) @RequestBody MemberUpdateRequest memberDto,
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        ResponseCode responseCode = ResponseCode.MEMBER_UPDATE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.updateInfo(memberDto, accessToken))
                        .build());
    }

    // 회원 정보 조회
    @GetMapping("/info")
    public ResponseEntity<ResResult> readInfo(
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        ResponseCode responseCode = ResponseCode.MEMBER_DETAIL;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.readInfo(accessToken))
                        .build());
    }


    // 회원 탈퇴
    @DeleteMapping
    public ResponseEntity<ResResult> signOut(
            @Validated({ValidationSequence.class}) @RequestBody MemberDeleteRequest memberDto,
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        memberService.signOut(memberDto, accessToken);
        ResponseCode responseCode = ResponseCode.MEMBER_DELETE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .build());
    }


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResResult> login(
            @Validated({ValidationSequence.class}) @RequestBody MemberLoginRequest memberDto
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


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResResult> logout(
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        ResponseCode responseCode = ResponseCode.MEMBER_LOGOUT;
        memberService.logout(accessToken);

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .build()
        );
    }


    // 아이디 찾기
    @PostMapping("/vertification/id")
    public ResponseEntity<ResResult> vertificationId(
            @Validated({ValidationSequence.class}) @RequestBody MemberVertifIdRequest memberDto
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


    // 비밀번호 찾기
    @PostMapping("/vertification/pw")
    public ResponseEntity<ResResult> vertificationPw(
            @Validated({ValidationSequence.class}) @RequestBody MemberVertifPwRequest memberDto
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


    // 프로필 생성
    @PostMapping("/info/profile")
    public ResponseEntity<ResResult> createProfile(
            @Validated({ValidationSequence.class}) @RequestBody MemberProfileCreateRequest memberDto,
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        ResponseCode responseCode = ResponseCode.MEMBER_PROFILE_CREATE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.createProfile(memberDto, accessToken))
                        .build()
        );
    }


    // 프로필 수정
    @PatchMapping("/info/profile")
    public ResponseEntity<ResResult> updateProfile(
            @Validated({ValidationSequence.class}) @RequestBody MemberProfileUpdateRequest memberDto,
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        ResponseCode responseCode = ResponseCode.MEMBER_PROFILE_UPDATE;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.updateProfile(memberDto, accessToken))
                        .build()
        );
    }


    // 프로필 조회
    @GetMapping("/info/profile")
    public ResponseEntity<ResResult> readProfile(
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        ResponseCode responseCode = ResponseCode.MEMBER_PROFILE_READ;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.readProfile(accessToken))
                        .build()
        );
    }

    // 사용자 프로필 존재 유무 체크
    @PostMapping("/info/profile/exists")
    public ResponseEntity<ResResult> isExistsProfile(
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        ResponseCode responseCode = ResponseCode.MEMBER_PROFILE_READ;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.existsUserProfile(accessToken))
                        .build()
        );
    }

    // 레벨업
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


    // 멤버 게임 기록 조회
    @GetMapping("/record")
    public ResponseEntity<ResResult> readRecord(
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        ResponseCode responseCode = ResponseCode.MEMBER_GAME_RECORD_READ;

        return ResponseEntity.ok(
                ResResult.builder()
                        .responseCode(responseCode)
                        .code(responseCode.getCode())
                        .message(responseCode.getMessage())
                        .data(memberService.readRecord(accessToken))
                        .build()
        );
    }


    // 멤버 게임 기록 업데이트
    @PatchMapping("/record")
    public ResponseEntity<ResResult> updateRecord(
            // 테스트를 위해 param으로 받음 -> 추후 @RequestParam 삭제 필요
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
