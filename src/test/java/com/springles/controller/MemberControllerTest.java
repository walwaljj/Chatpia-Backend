package com.springles.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springles.controller.api.MemberController;
import com.springles.domain.constants.GameRole;
import com.springles.domain.constants.Level;
import com.springles.domain.constants.ProfileImg;
import com.springles.domain.constants.ResponseCode;
import com.springles.domain.dto.member.*;
import com.springles.domain.dto.response.ResResult;
import com.springles.domain.entity.Member;
import com.springles.domain.entity.MemberRecord;
import com.springles.domain.entity.RefreshToken;
import com.springles.service.MemberService;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

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
    @DisplayName("회원탈퇴 테스트 - CASE.성공")
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

        MemberLoginResponse returnValue = MemberLoginResponse.builder()
                .accessToken("accessTokenValue")
                .refreshToken(
                        RefreshToken.builder()
                                .Id("refreshTokenId")
                                .refreshToken("refreshTokenValue")
                                .expiration(60L)
                                .memberName("mafia1")
                                .build()
                )
                .memberName("mafia1")
                .build();

        // when
        when(memberService.login(any(MemberLoginRequest.class))).thenReturn(returnValue);

        // then
        mockMvc.perform(post("/member/login")
                        .content(objectMapper.writeValueAsString(memberDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().is2xxSuccessful(), // 상태코드 200
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.data.memberName").value("mafia1"),
                        MockMvcResultMatchers.jsonPath("$.data.accessToken").value("accessTokenValue"),
                        MockMvcResultMatchers.jsonPath("$.data.refreshToken.refreshToken").value("refreshTokenValue")
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

    @Test
    @DisplayName("프로필 생성 테스트 - CASE.성공")
    void createProfile() throws Exception {
        // given
        MemberProfileCreateRequest memberDto = MemberProfileCreateRequest.builder()
                .nickname("나는야마피아")
                .profileImgNum(1)
                .build();

        MemberProfileResponse returnValue = MemberProfileResponse.builder()
                .memberId(1L)
                .nickname("나는야마피아")
                .profileImg(ProfileImg.PROFILE01)
                .level(Level.BEGINNER)
                .exp(0L)
                .inGameRole(GameRole.NONE)
                .isObserver(false)
                .build();

        // when
        when(memberService.createProfile(any(MemberProfileCreateRequest.class), eq(authHeader))).thenReturn(returnValue);

        // then
        mockMvc.perform(post("/member/info/profile")
                        .content(objectMapper.writeValueAsString(memberDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader)
                )
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.data.nickname").value("나는야마피아"),
                        MockMvcResultMatchers.jsonPath("$.data.profileImg").value("PROFILE01"),
                        MockMvcResultMatchers.jsonPath("$.data.level").value("BEGINNER"),
                        MockMvcResultMatchers.jsonPath("$.data.exp").value("0"),
                        MockMvcResultMatchers.jsonPath("$.data.inGameRole").value("NONE"),
                        MockMvcResultMatchers.jsonPath("$.data.observer").value(false),
                        MockMvcResultMatchers.jsonPath("$.data.memberId").value(1L)
                );
    }


    @Test
    @DisplayName("프로필 수정 테스트 - CASE.성공")
    void updateProfile() throws Exception {
        // given
        MemberProfileUpdateRequest memberDto = MemberProfileUpdateRequest.builder()
                .nickname("나는야시민")
                .profileImgNum(2)
                .build();

        MemberProfileResponse returnValue = MemberProfileResponse.builder()
                .memberId(1L)
                .nickname("나는야시민")
                .profileImg(ProfileImg.PROFILE02)
                .level(Level.BEGINNER)
                .exp(0L)
                .inGameRole(GameRole.NONE)
                .isObserver(false)
                .build();

        // when
        when(memberService.updateProfile(any(MemberProfileUpdateRequest.class), eq(authHeader))).thenReturn(returnValue);

        // then
        mockMvc.perform(patch("/member/info/profile")
                        .content(objectMapper.writeValueAsString(memberDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader)
                )
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.data.nickname").value("나는야시민"),
                        MockMvcResultMatchers.jsonPath("$.data.profileImg").value("PROFILE02"),
                        MockMvcResultMatchers.jsonPath("$.data.level").value("BEGINNER"),
                        MockMvcResultMatchers.jsonPath("$.data.exp").value("0"),
                        MockMvcResultMatchers.jsonPath("$.data.inGameRole").value("NONE"),
                        MockMvcResultMatchers.jsonPath("$.data.observer").value(false),
                        MockMvcResultMatchers.jsonPath("$.data.memberId").value(1L)
                );
    }

    @Test
    @DisplayName("프로필 조회 테스트 - CASE.성공")
    void readProfile() throws Exception {
        // given
        MemberProfileRead returnValue = MemberProfileRead.builder()
                .nickname("나는야마피아")
                .profileImg(ProfileImg.PROFILE01)
                .level("BEGINNER")
                .exp(0L)
                .nextLevel("ASSOCIATE")
                .rank(1L)
                .build();

        // when
        when(memberService.readProfile(authHeader)).thenReturn(returnValue);

        // then
        mockMvc.perform(get("/member/info/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader)
                )
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.data.nickname").value("나는야마피아"),
                        MockMvcResultMatchers.jsonPath("$.data.profileImg").value("PROFILE01"),
                        MockMvcResultMatchers.jsonPath("$.data.level").value("BEGINNER"),
                        MockMvcResultMatchers.jsonPath("$.data.exp").value("0"),
                        MockMvcResultMatchers.jsonPath("$.data.nextLevel").value("ASSOCIATE"),
                        MockMvcResultMatchers.jsonPath("$.data.rank").value(1L)
                );
    }


    @Test
    @DisplayName("레벨업 테스트 - CASE.성공")
    void levelup() throws Exception {
        // given
        MemberProfileResponse returnValue = MemberProfileResponse.builder()
                .memberId(1L)
                .nickname("나는야마피아")
                .profileImg(ProfileImg.PROFILE01)
                .level(Level.ASSOCIATE)
                .exp(2000L)
                .inGameRole(GameRole.NONE)
                .isObserver(false)
                .build();

        // when
        when(memberService.levelUp(1L)).thenReturn(returnValue);

        // then
        mockMvc.perform(patch("/member/levelup")
                .param("memberId", "1")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.data.nickname").value("나는야마피아"),
                        MockMvcResultMatchers.jsonPath("$.data.profileImg").value("PROFILE01"),
                        MockMvcResultMatchers.jsonPath("$.data.level").value("ASSOCIATE"),
                        MockMvcResultMatchers.jsonPath("$.data.exp").value(2000L),
                        MockMvcResultMatchers.jsonPath("$.data.inGameRole").value("NONE"),
                        MockMvcResultMatchers.jsonPath("$.data.observer").value(false),
                        MockMvcResultMatchers.jsonPath("$.data.memberId").value(1L)
                );
    }


    @Test
    @DisplayName("멤버 게임 기록 조회 테스트 - CASE.성공")
    void readRecord() throws Exception {
        // given
        MemberRecordResponse returnValue = MemberRecordResponse.builder().id(1L).memberId(1L)
                .mafiaCnt(1L).citizenCnt(0L).policeCnt(0L).doctorCnt(0L).citizenWinCnt(0L).mafiaWinCnt(1L)
                .saveCnt(0L).killCnt(2L).totalCnt(1L).totalTime(30L).build();

        // when
        when(memberService.readRecord(authHeader)).thenReturn(returnValue);

        // then
        mockMvc.perform(get("/member/record")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("data.mafiaCnt").value(1L),
                        MockMvcResultMatchers.jsonPath("data.citizenCnt").value(0L),
                        MockMvcResultMatchers.jsonPath("data.policeCnt").value(0L),
                        MockMvcResultMatchers.jsonPath("data.doctorCnt").value(0L),
                        MockMvcResultMatchers.jsonPath("data.citizenWinCnt").value(0L),
                        MockMvcResultMatchers.jsonPath("data.mafiaWinCnt").value(1L),
                        MockMvcResultMatchers.jsonPath("data.saveCnt").value(0L),
                        MockMvcResultMatchers.jsonPath("data.killCnt").value(2L),
                        MockMvcResultMatchers.jsonPath("data.totalCnt").value(1L),
                        MockMvcResultMatchers.jsonPath("data.totalTime").value(30L)
                );
    }


    @Test
    @DisplayName("멤버 게임 기록 업데이트 테스트 - CASE.성공")
    void updateRecord() throws Exception {
        // given
        MemberRecordResponse returnValue = MemberRecordResponse.builder().id(1L).memberId(1L)
                .mafiaCnt(1L).citizenCnt(0L).policeCnt(0L).doctorCnt(0L).citizenWinCnt(0L).mafiaWinCnt(1L)
                .saveCnt(0L).killCnt(2L).totalCnt(1L).totalTime(30L).build();

        // when
        when(memberService.updateRecord(1L)).thenReturn(returnValue);

        // then
        mockMvc.perform(patch("/member/record")
                        .param("memberId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("data.mafiaCnt").value(1L),
                        MockMvcResultMatchers.jsonPath("data.citizenCnt").value(0L),
                        MockMvcResultMatchers.jsonPath("data.policeCnt").value(0L),
                        MockMvcResultMatchers.jsonPath("data.doctorCnt").value(0L),
                        MockMvcResultMatchers.jsonPath("data.citizenWinCnt").value(0L),
                        MockMvcResultMatchers.jsonPath("data.mafiaWinCnt").value(1L),
                        MockMvcResultMatchers.jsonPath("data.saveCnt").value(0L),
                        MockMvcResultMatchers.jsonPath("data.killCnt").value(2L),
                        MockMvcResultMatchers.jsonPath("data.totalCnt").value(1L),
                        MockMvcResultMatchers.jsonPath("data.totalTime").value(30L)
                );
    }
}