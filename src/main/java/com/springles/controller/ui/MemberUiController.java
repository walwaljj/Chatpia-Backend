package com.springles.controller.ui;

import com.springles.domain.dto.member.*;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.jwt.JwtTokenUtils;
import com.springles.repository.MemberGameInfoJpaRepository;
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
    private final MemberGameInfoJpaRepository memberGameInfoJpaRepository;
    private final JwtTokenUtils jwtTokenUtils;

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

    // 로그인 POST
    @PostMapping("/login")
    public String signup(@ModelAttribute("memberDto") @Validated({ValidationSequence.class}) MemberLoginRequest memberDto, HttpServletResponse response) {
        // 로그인 성공, Token 정보 받기
        MemberLoginResponse memberLoginResponse = memberService.login(memberDto);
        // AccessToken Cookie에 저장
        String accessToken = memberLoginResponse.getAccessToken();
        setAtkCookie("accessToken", accessToken, response);
        // RefreshToken id값 Cookie에 저장
        String refreshTokenId = memberLoginResponse.getRefreshToken().getId();
        setRtkCookie("refreshTokenId", refreshTokenId, response);

        // 프로필 정보가 있으면 index로 이동, 없으면 프로필 설정 화면으로 이동
        Long memberId = memberService.getUserInfo(accessToken).getId();
        if (memberGameInfoJpaRepository.existsByMemberId(memberId)) {
            return "redirect:index";
        }

        return "redirect:profile-settings";
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
        String accessToken = jwtTokenUtils.atkFromCookie(request);

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
        String accessToken = jwtTokenUtils.atkFromCookie(request);
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
        String accessToken = jwtTokenUtils.atkFromCookie(request);
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


    // accessToken 쿠키 설정
    public void setAtkCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 1시간(테스트용)
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);   // 사파리 브라우저에서 쿠키 저장이 안되는 이슈 해결을 위해 설정 해제
        response.addCookie(cookie);
    }


    // refreshToken 쿠키 설정
    public void setRtkCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);    // 2주
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);   // 사파리 브라우저에서 쿠키 저장이 안되는 이슈 해결을 위해 설정 해제
        response.addCookie(cookie);
    }
}
