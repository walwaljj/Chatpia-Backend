package com.springles.controller.ui;

import com.springles.domain.dto.member.*;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.jwt.JwtTokenUtils;
import com.springles.repository.MemberGameInfoJpaRepository;
import com.springles.service.CookieService;
import com.springles.service.MemberService;
import com.springles.valid.ValidationSequence;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("v1")
@RequiredArgsConstructor
@Slf4j
public class MemberUiController {

    private final MemberService memberService;
    private final CookieService cookieService;

    // 회원가입 페이지 조회
    @GetMapping("/signup")
    public String signUpPage() {
        return "member/sign-up";
    }


    // 로그인 페이지 조회
    @GetMapping("/login-page")
    public String loginPage(Model model, MemberLoginRequest memberDto) {
        model.addAttribute("memberDto", memberDto);
        return "member/login";
    }


    // 아이디 찾기 페이지 조회
    @GetMapping("/vertification-id")
    public String vertificationId() {
        return "member/vertification-id";
    }


    // 비밀번호 찾기 페이지 조회
    @GetMapping("/vertification-pw")
    public String vertificationPw() {
        return "member/vertification-pw";
    }


    // 마이페이지 조회
    @GetMapping("/my-page")
    public String memberProflie(
            Model model,
            HttpServletRequest request
    ) {
        // accessToken 추출
        String accessToken = cookieService.atkFromCookie(request);

        // 프로필 조회
        MemberProfileRead profileInfo = memberService.readProfile(accessToken);

        // 멤버 게임기록 조회
        MemberRecordResponse memberRecord = memberService.readRecord(accessToken);

        model.addAttribute("profileInfo", profileInfo);
        model.addAttribute("record", memberRecord);

        return "member/my-page";
    }


    // 회원 정보 변경 페이지 조회
    @GetMapping("/my-page/info")
    public String memberInfo(
            Model model,
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        MemberInfoResponse memberInfo = memberService.getUserInfo(accessToken);
        model.addAttribute("rawMemberInfo", memberInfo);

        return "member/member-info";
    }

    // 회원 탈퇴 페이지 조회
    @GetMapping("/my-page/sign-out")
    public String signOut() {
        return "member/member-sign-out";
    }


    // 프로필 설정 페이지 조회
    @GetMapping("/profile-settings")
    public String profileSetting() {
        return "member/profile-settings";
    }


    // 프로필 변경 페이지 조회
    @GetMapping("/profile-change")
    public String profileSetting(
            Model model,
            @ModelAttribute("profile") MemberProfileUpdateRequest memberDto,
            HttpServletRequest request
    ) {
        String accessToken = cookieService.atkFromCookie(request);
        MemberProfileRead rawProfile = memberService.readProfile(accessToken);

        model.addAttribute("rawProfile", rawProfile);
        return "member/profile-change";
    }


    /** 메소드 */
    // 사용자 정보 요청
    public MemberInfoResponse info(String authHeader) {
        return memberService.getUserInfo(authHeader);
    }


    // 사용자 프로필 정보 요청
    public MemberProfileResponse profileInfo(String accessToken) {
        return memberService.getUserProfileInfo(accessToken);
    }
}
