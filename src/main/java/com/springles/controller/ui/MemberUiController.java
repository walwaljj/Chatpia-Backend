package com.springles.controller.ui;
import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.dto.member.MemberInfoResponse;
import com.springles.domain.dto.member.MemberLoginRequest;
import com.springles.domain.dto.member.MemberLoginResponse;
import com.springles.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("v1")
@RequiredArgsConstructor
@Slf4j
public class MemberUiController {

    private final MemberService memberService;

    // 회원가입 페이지 조회
    @GetMapping("/signup")
    public String signUpPage(Model model, MemberCreateRequest memberDto) {
        model.addAttribute("memberDto", memberDto);
        return "member/sign-up";
    }

    // 회원가입 POST 요청
    @PostMapping("/signup")
    public String signup(Model model, @Valid MemberCreateRequest memberDto
    ) {
        model.addAttribute("memberDto", memberDto);
        memberService.signUp(memberDto);
        return "redirect:login-page";
    }

    // 로그인 페이지 조회
    @GetMapping("/login-page")
    public String loginPage(Model model, MemberLoginRequest memberDto) {
        model.addAttribute("memberDto", memberDto);
        return "member/login";
    }

    // 로그인 POST
    @PostMapping("/login")
    public String signup(@ModelAttribute("memberDto") @Valid MemberLoginRequest memberDto, HttpServletResponse response)
    {
        // 로그인 성공, Token 정보 받기
        MemberLoginResponse memberLoginResponse = memberService.login(memberDto);
        // AccessToken Cookie에 저장
        String accessToken = memberLoginResponse.getAccessToken();
        setAtkCookie("accessToken", accessToken, response);
        // RefreshToken id값 Cookie에 저장
        String refreshTokenId = memberLoginResponse.getRefreshToken().getId();
        setRtkCookie("refreshTokenId", refreshTokenId, response);

        return "redirect:index";
    }

    // 로그아웃 요청
    @PostMapping("/logout")
    public String logout(
            HttpServletRequest request
//            @RequestHeader(value = "Authorization") String authHeader
    ) {
        Cookie[] cookies = request.getCookies();
        String authHeader = cookies[0].getValue();
        memberService.logout(authHeader);

        return "redirect:index";
    }

    // 사용자 정보 요청
    @PostMapping("/info")
    public MemberInfoResponse info(String authHeader) {
        return memberService.getUserInfo(authHeader);
    }

    // 쿠키 설정
//    public void setCookie(String name, String value, HttpServletResponse response) {
//        Cookie cookie = new Cookie(name, value);
//        cookie.setDomain("localhost");
//        cookie.setPath("/");
//        cookie.setMaxAge(60*60);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        response.addCookie(cookie);
//    }

    // accessToken 쿠키 설정
    public void setAtkCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 1시간(테스트용)
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    // refreshToken 쿠키 설정
    public void setRtkCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);    // 2주
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
