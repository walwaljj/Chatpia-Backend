package com.springles.controller.ui;
import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.dto.member.MemberLoginRequest;
import com.springles.service.MemberService;
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

    // 회원가입 페이지
    @GetMapping("/signup")
    public String signUpPage(Model model, MemberCreateRequest memberDto) {
        model.addAttribute("memberDto", memberDto);
        return "member/sign-up";
    }
    // 회원가입 POST
    @PostMapping("/signup")
    public String signup(Model model, @Valid MemberCreateRequest memberDto
    ) {
        model.addAttribute("memberDto", memberDto);
        memberService.signUp(memberDto);
        return "redirect:login";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage(Model model, MemberLoginRequest memberDto) {
        model.addAttribute("memberDto", memberDto);
        return "member/login";
    }
    // 로그인 POST
    @PostMapping("/login")
    public String signup(@ModelAttribute("memberDto")  @Valid @RequestBody MemberLoginRequest memberDto
    ) {
        String whtisit = memberService.login(memberDto);
        log.info(whtisit);
        return "redirect:index";
    }

}
