package com.springles.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springles.controller.api.MemberController;
import com.springles.domain.dto.member.*;
import com.springles.domain.entity.Member;
import com.springles.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    String authHeader = "Bearer diodniwnfopewmfpamfpomeopmqoefmqoemjfpo";

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    @DisplayName("회원가입 테스트  - CASE.성공")
    void signup() throws Exception {
        // given
        // MemberService.signup에 요청할 memberDto
        MemberCreateRequest memberDto = MemberCreateRequest.builder()
                .memberName("mafia1")
                .password("password1!")
                .passwordConfirm("password1!")
                .email("mafia1@gmail.com")
                .build();

        // MemberService.signup에서 반환할 문자열
        String returnValue = MemberCreateRequest.fromEntity(memberDto.newMember(passwordEncoder)).toString();

        // when
        // memberService.signup이 동작한다고 가정
        when(memberService.signUp(any(MemberCreateRequest.class))).thenReturn(returnValue);

        // then
        //perform() : http 요청을 보낸 것을 시뮬레이션 하여 userController에게 요청
        mockMvc.perform(post("/member/signup")
                        .content(objectMapper.writeValueAsString(memberDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().is2xxSuccessful(), // 상태코드 200
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.data").value(returnValue)
                );
    }


    @Test
    @DisplayName("회원 정보 수정 테스트 - CASE.성공")
    void updateInfo() throws Exception {
        // given
        MemberUpdateRequest memberDto = MemberUpdateRequest.builder()
                .password("updatepassword1!")
                .passwordConfirm("updatepassword1!")
                .email("updateMafia@gmail.com")
                .build();

        String returnValue = Member.builder()
                .id(1L)
                .memberName("mafia1")
                .password(passwordEncoder.encode("updatePassword1!"))
                .email("updateMafia1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build()
                .toString();

        // when
        when(memberService.updateInfo(any(MemberUpdateRequest.class), eq(authHeader))).thenReturn(returnValue);

        // then
        mockMvc.perform(patch("/member/info")
                        .content(objectMapper.writeValueAsString(memberDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader)
                )
                .andExpectAll(
                        status().is2xxSuccessful(), // 상태코드 200
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.data").value(returnValue)
                );
    }

    @Test
    @DisplayName("로그아웃 테스트 - CASE.성공")
    void signOut() throws Exception {
        // given
        MemberDeleteRequest memberDto = MemberDeleteRequest.builder()
                .password("password1!")
                .build();

        // when
        doNothing().when(memberService).signOut(any(MemberDeleteRequest.class), eq(authHeader));

        // then
        mockMvc.perform(delete("/member")
                        .content(objectMapper.writeValueAsString(memberDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader)
                )
                .andExpectAll(
                        status().is2xxSuccessful(), // 상태코드 200
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }


    @Test
    @DisplayName("로그인 테스트 - CASE. 성공")
    void login() throws Exception {
        // given
        MemberLoginRequest memberDto = MemberLoginRequest.builder()
                .memberName("mafia1")
                .password("password1!")
                .build();

        String returnValue =
                "memberName : " + memberDto.getMemberName()
                        + ", accessToken : " + "accessTokenValue"
                        + ", refreshToken : "
                        + "{ "
                        + "id : " + 1L
                        + ", refreshToken : " + "refreshTokenValue"
                        + ", expiryDate : " + 60
                        + " }";

        // when
        when(memberService.login(any(MemberLoginRequest.class)).thenReturn(returnValue);

        // then
        mockMvc.perform(post("/member/login")
                        .content(objectMapper.writeValueAsString(memberDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().is2xxSuccessful(), // 상태코드 200
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.data").value(returnValue)
                );
    }


    @Test
    @DisplayName("로그아웃 테스트 - CASE.성공")
    void logout() throws Exception {

        // when
        doNothing().when(memberService).logout(eq(authHeader));

        // then
        mockMvc.perform(post("/member/logout")
                        .header("Authorization", authHeader)
                )
                .andExpect(
                        status().is2xxSuccessful() // 상태코드 200
                );
    }

    @Test
    @DisplayName("아이디 찾기 테스트 - CASE.성공")
    void vertificationId() throws Exception {
        // given
        MemberVertifIdRequest memberDto = MemberVertifIdRequest.builder()
                .email("mafia1@gmail.com")
                .build();

        String returnValue =
                "memberName : " + "[mafia1]"
                        + ", email : " + memberDto.getEmail();

        // when
        when(memberService.vertificationId(any(MemberVertifIdRequest.class))).thenReturn(returnValue);

        // then
        mockMvc.perform(post("/member/vertification/id")
                        .content(objectMapper.writeValueAsString(memberDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().is2xxSuccessful(), // 상태코드 200
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.data").value(returnValue)
                );
    }


    @Test
    @DisplayName("비밀번호 찾기 테스트 - CASE.성공")
    void vertificationPw() throws Exception {
        // given
        MemberVertifPwRequest memberDto = MemberVertifPwRequest.builder()
                .memberName("mafia1")
                .email("mafia1@gmail.com")
                .build();

        String returnValue =
                "memberName" + memberDto.getMemberName()
                        + ", password" + passwordEncoder.encode(memberService.randomPassword())
                        + ", email : " + memberDto.getEmail();

        // when
        when(memberService.vertificationPw(any(MemberVertifPwRequest.class))).thenReturn(returnValue);

        // then
        mockMvc.perform(post("/member/vertification/pw")
                        .content(objectMapper.writeValueAsString(memberDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().is2xxSuccessful(), // 상태코드 200
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.data").value(returnValue)
                );
    }
}