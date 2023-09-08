package com.springles.controller.ui;

import com.springles.domain.dto.member.*;
import com.springles.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("v1")
@RequiredArgsConstructor
@Slf4j
public class MemberUiController {

    private final MemberService memberService;

    // 회원가입 페이지 조회
    @GetMapping("/signup")
    public String signUpPage() {
        return "member/sign-up";
    }


    // 로그인 페이지 조회
    @GetMapping("/login-page")
    public String loginPage() {
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
    public String memberProfile() {
        return "member/my-page";
    }


    // 회원 정보 변경 페이지 조회
    @GetMapping("/my-page/info")
    public String memberInfo() {
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
    public String profileChange() {
        return "member/profile-change";
    }


    /** 메소드 */
    // 사용자 정보 요청
    public MemberInfoResponse info(String authHeader) {
        return memberService.getUserInfo(authHeader);
    }
}
