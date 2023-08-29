package com.springles.service.impl;

import com.springles.domain.dto.member.MemberLoginRequest;
import com.springles.domain.dto.member.MemberLoginResponse;
import com.springles.domain.entity.Member;
import com.springles.jwt.JwtTokenUtils;
import com.springles.repository.MemberJpaRepository;
import com.springles.service.MemberService;
import com.springles.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RefreshTokenServiceImplTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtils jwtTokenUtils;

    String accessToken = "";
    String refreshTokenId = "";
    String authHeader = "";

    /** 초기화 데이터
     * 1. 회원가입 및 로그인 한 사용자 생성
     * */
    @BeforeEach
    void init() {
        memberJpaRepository.save(Member.builder()
                .memberName("mafia1")
                .password(passwordEncoder.encode("password1!"))
                .email("mafia1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        MemberLoginRequest memberLoginRequest = MemberLoginRequest.builder()
                .memberName("mafia1")
                .password("password1!")
                .build();

        MemberLoginResponse loginInfo = memberService.login(memberLoginRequest);
        refreshTokenId = loginInfo.getRefreshToken().getId();
    }

    /** 테스트 항목
     * 1. refreshTokenId로 재발급된 accssToken이 정상적인가(jwt parsing이 잘 되는가)
     * */
    @Test
    @DisplayName("accessToken 재발급 테스트 - CASE.성공")
    void reissue() {
        // given - when
        String newAccessToken = refreshTokenService.reissue(refreshTokenId);

        // then
        assertEquals("mafia1", jwtTokenUtils.parseClaims(newAccessToken).getSubject());
    }
}