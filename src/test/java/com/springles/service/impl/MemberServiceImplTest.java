package com.springles.service.impl;

import com.springles.domain.dto.member.*;
import com.springles.domain.entity.Member;
import com.springles.domain.entity.RefreshToken;
import com.springles.jwt.JwtTokenUtils;
import com.springles.repository.BlackListTokenRedisRepository;
import com.springles.repository.MemberJpaRepository;
import com.springles.repository.RefreshTokenRedisRepository;
import com.springles.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceImplTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    BlackListTokenRedisRepository blackListTokenRedisRepository;

    @Autowired
    RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    JwtTokenUtils jwtTokenUtils;

    String authHeader = "";
    String accessToken = "";


    /** 초기화 데이터
     * 1. 회원가입 및 로그인 한 사용자 생성
     * */
    @BeforeEach
    void init() {
        memberJpaRepository.save(Member.builder()
                .memberName("user1")
                .password(passwordEncoder.encode("password1!"))
                .email("user1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        MemberLoginRequest memberLoginRequest = MemberLoginRequest.builder()
                .memberName("user1")
                .password("password1!")
                .build();

        String loginInfo = memberService.login(memberLoginRequest);
        accessToken = loginInfo.split(" ")[5].split(",")[0];
        authHeader = "Bearer " + accessToken;
    }


    /** 테스트 항목
     * 1. DB에 해당 회원정보가 정상적으로 저장되는가
     * */
    @Test
    @DisplayName("회원가입 테스트")
    void signUp() {
        // given
        MemberCreateRequest memberDto = MemberCreateRequest.builder()
                .memberName("user2")
                .password("password2!")
                .passwordConfirm("password2!")
                .email("user2@gmail.com")
                .build();

        // when
        memberService.signUp(memberDto);
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("user2");

        // then
        assertNotNull(optionalMember.get().getId());
        assertTrue(passwordEncoder.matches("password2!", optionalMember.get().getPassword()));
        assertEquals(optionalMember.get().getEmail(), "user2@gmail.com");
        assertEquals(optionalMember.get().getRole(), "USER");
        assertFalse(optionalMember.get().getIsDeleted());
    }


    /** 테스트 항목
     * 1. 변경한 정보가 DB에 정상적으로 저장되는가
     * */
    @Test
    @DisplayName("회원 정보 변경 테스트")
    void updateInfo() {
        // given
        MemberUpdateRequest memberDto = MemberUpdateRequest.builder()
                .password("updatepassword1!")
                .passwordConfirm("updatepassword1!")
                .email("updateuser1@gmail.com")
                .build();

        // when
        memberService.updateInfo(memberDto, authHeader);
        Optional<Member> optionalUpdateMember = memberJpaRepository.findByMemberName("user1");

        // then
        assertEquals(optionalUpdateMember.get().getEmail(), "updateuser1@gmail.com");
        assertTrue(passwordEncoder.matches("updatepassword1!", optionalUpdateMember.get().getPassword()));
    }


    /** 테스트 항목
     * 1.  해당 회원의 isDeleted 값이 true로 변경되는가
     * */
    @Test
    @DisplayName("회원 탈퇴 테스트")
    void signOut() {
        // given
        MemberDeleteRequest memberDto = MemberDeleteRequest.builder()
                .password("password1!")
                .build();

        // when
        memberService.signOut(memberDto, authHeader);
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("user1");

        // then
        assertTrue(optionalMember.get().getIsDeleted());
    }


    /** 테스트 항목
     * 1. accessToken이 정상적으로 생성되는가(jwt parsing이 잘 되는가)
     * 2. refreshToken이 정상적으로 생성되며, redis에 저장되는가
     * */
    @Test
    @DisplayName("로그인 테스트")
    void login() {
        // given - when
        String result = memberService.login(MemberLoginRequest.builder()
                .memberName("user1")
                .password("password1!")
                .build()
        );

        String accessToken = result.split("accessToken : ")[1].split(",")[0];
        String refreshTokenId = result.split("refreshToken : \\{ id : ")[1].split(",")[0];

        // then
        assertEquals("user1", jwtTokenUtils.parseClaims(accessToken).getSubject());
        assertTrue(refreshTokenRedisRepository.existsById(refreshTokenId));
    }


    /** 테스트 항목
     * 1. accessToken이 블랙리스트(redis)로 저장되는가
     * 2. refreshToken이 redis에서 삭제되는가
     * */
    @Test
    @DisplayName("로그아웃 테스트")
    void logout() {
        // given - when
        memberService.logout(authHeader);

        boolean IsBlackListToken = blackListTokenRedisRepository.existsByAccessToken(accessToken);
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRedisRepository.findByMemberName("user1");

        // then
        assertTrue(IsBlackListToken);
        assertTrue(optionalRefreshToken.isEmpty());
    }


    /** 테스트 항목
     * 1. 수신자 email이 정상적으로 반환되는가
     * 2. 수신자 email로 가입된 memberName이 정상적으로 반환되는가
     * (반환값이 정상이다 = 메일 전송이 정상적으로 완료되었다)
     * */
    @Test
    @DisplayName("아이디 찾기 테스트")
    void vertificationId() {
        // given
        memberService.signUp(
                MemberCreateRequest.builder()
                        .memberName("user2")
                        .password("password2!")
                        .passwordConfirm("password2!")
                        .email("user2@gmail.com")
                        .build()
        );

        // when
        String result = memberService.vertificationId(
                MemberVertifIdRequest.builder()
                        .email("user2@gmail.com")
                        .build()
        );

        // then
        assertEquals("user2@gmail.com", result.split("email : ")[1]);
        assertEquals("user2", result.split("\\[")[1].split("]")[0]);
    }


    /** 테스트 항목
     * 1. 수신자 email이 정상적으로 반환되는가
     * 2. 수신자 email로 가입된 memberName이 정상적으로 반환되는가
     * (반환값이 정상이다 = 메일 전송이 정상적으로 완료되었다)
     * 3. db에 임시 비밀번호가 정상적으로 저장되는가(기존 비밀번호와 다른 값이 저장되어 있는가)
     * */
    @Test
    @DisplayName("비밀번호 찾기 테스트")
    void vertificationPw() {
        // given
        memberService.signUp(
                MemberCreateRequest.builder()
                        .memberName("user2")
                        .password("password2!")
                        .passwordConfirm("password2!")
                        .email("user2@gmail.com")
                        .build()
        );

        // when
        String result = memberService.vertificationPw(
                MemberVertifPwRequest.builder()
                        .memberName("user2")
                        .email("user2@gmail.com")
                        .build()
        );

        // then
        assertEquals("user2@gmail.com", result.split("email : ")[1]);
        assertEquals("user2", result.split(" ")[2].split(",")[0]);
        assertFalse(passwordEncoder.matches("password2!", result.split(" ")[5].split(",")[0]));
    }


    /** 테스트 항목
     * 1. 매번 랜덤 생성이 되는가
     * 2. 생성된 비밀번호의 글자수는 8자인가
     * */
    @Test
    @DisplayName("임시 비밀번호 생성 테스트")
    void randomPassword() {
        // given - when
        String firstPassword = memberService.randomPassword();
        String secondPassword = memberService.randomPassword();

        // then
        assertNotEquals(firstPassword, secondPassword);
        assertEquals(firstPassword.length(), 8);
        assertEquals(secondPassword.length(), 8);
    }
}