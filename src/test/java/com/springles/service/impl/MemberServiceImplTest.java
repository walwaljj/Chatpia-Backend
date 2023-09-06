/*
package com.springles.service.impl;

import com.springles.domain.constants.GameRole;
import com.springles.domain.constants.Level;
import com.springles.domain.constants.ProfileImg;
import com.springles.domain.dto.member.*;
import com.springles.domain.entity.*;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.jwt.JwtTokenUtils;
import com.springles.repository.*;
import com.springles.repository.support.MemberGameInfoJpaRepository;
import com.springles.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
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
    MemberGameInfoJpaRepository memberGameInfoJpaRepository;

    @Autowired
    GameRecordJpaRepository gameRecordJpaRepository;

    @Autowired
    MemberRecordJpaRepository memberRecordJpaRepository;

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


    */
/** 초기화 데이터
     * 1. 회원가입 및 로그인 한 사용자 생성
     * *//*

    @BeforeEach
    void init() {
        memberService.signUp(
                MemberCreateRequest.builder()
                        .memberName("mafia1")
                        .password("password1!")
                        .passwordConfirm("password1!")
                        .email("mafia1@gmail.com")
                        .build()
        );

        MemberLoginRequest memberLoginRequest = MemberLoginRequest.builder()
                .memberName("mafia1")
                .password("password1!")
                .build();

        MemberLoginResponse loginInfo = memberService.login(memberLoginRequest);
        accessToken = loginInfo.getAccessToken();
        authHeader = "Bearer " + accessToken;
    }

    @AfterEach
    void DeleteAll() {
        memberJpaRepository.deleteAll();
        blackListTokenRedisRepository.deleteAll();
        refreshTokenRedisRepository.deleteAll();
        memberGameInfoJpaRepository.deleteAll();
        gameRecordJpaRepository.deleteAll();
    }


    */
/** 테스트 항목
     * 1. DB에 해당 회원정보가 정상적으로 저장되는가
     * *//*

    @Test
    @DisplayName("회원가입 테스트 - CASE.성공")
    void signUp() {
        // given
        MemberCreateRequest memberDto = MemberCreateRequest.builder()
                .memberName("mafia2")
                .password("password2!")
                .passwordConfirm("password2!")
                .email("mafia2@gmail.com")
                .build();

        // when
        memberService.signUp(memberDto);
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia2");

        // then
        assertNotNull(optionalMember.get().getId());
        assertTrue(passwordEncoder.matches("password2!", optionalMember.get().getPassword()));
        assertEquals(optionalMember.get().getEmail(), "mafia2@gmail.com");
        assertEquals(optionalMember.get().getRole(), "USER");
        assertFalse(optionalMember.get().getIsDeleted());
    }


    */
/** 테스트 항목
     * 1. 변경한 정보가 DB에 정상적으로 저장되는가
     * *//*

    @Test
    @DisplayName("회원 정보 변경 테스트 - CASE.성공")
    void updateInfo() {
        // given
        MemberUpdateRequest memberDto = MemberUpdateRequest.builder()
                .password("updatepassword1!")
                .passwordConfirm("updatepassword1!")
                .email("updatemafia1@gmail.com")
                .build();

        // when
        memberService.updateInfo(memberDto, authHeader);
        Optional<Member> optionalUpdateMember = memberJpaRepository.findByMemberName("mafia1");

        // then
        assertEquals(optionalUpdateMember.get().getEmail(), "updatemafia1@gmail.com");
        assertTrue(passwordEncoder.matches("updatepassword1!", optionalUpdateMember.get().getPassword()));
    }


    */
/** 테스트 항목
     * 1.  해당 회원의 isDeleted 값이 true로 변경되는가
     * *//*

    @Test
    @DisplayName("회원 탈퇴 테스트 - CASE.성공")
    void signOut() {
        // given
        MemberDeleteRequest memberDto = MemberDeleteRequest.builder()
                .password("password1!")
                .build();

        // when
        memberService.signOut(memberDto, authHeader);
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia1");

        // then
        assertTrue(optionalMember.get().getIsDeleted());
    }


    */
/** 테스트 항목
     * 1. accessToken이 정상적으로 생성되는가(jwt parsing이 잘 되는가)
     * 2. refreshToken이 정상적으로 생성되며, redis에 저장되는가
     * *//*

    @Test
    @DisplayName("로그인 테스트 - CASE.성공")
    void login() {
        // given - when
        MemberLoginResponse result = memberService.login(MemberLoginRequest.builder()
                .memberName("mafia1")
                .password("password1!")
                .build()
        );

        String accessToken = result.getAccessToken();
        String refreshTokenId = result.getRefreshToken().getId();

        // then
        assertEquals("mafia1", jwtTokenUtils.parseClaims(accessToken).getSubject());
        assertTrue(refreshTokenRedisRepository.existsById(refreshTokenId));
    }


    */
/** 테스트 항목
     * 1. accessToken이 블랙리스트(redis)로 저장되는가
     * 2. refreshToken이 redis에서 삭제되는가
     * *//*

    @Test
    @DisplayName("로그아웃 테스트 - CASE.성공")
    void logout() {
        // given - when
        memberService.logout(authHeader);

        boolean IsBlackListToken = blackListTokenRedisRepository.existsByAccessToken(accessToken);
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRedisRepository.findByMemberName("mafia1");

        // then
        assertTrue(IsBlackListToken);
        assertTrue(optionalRefreshToken.isEmpty());
    }


    */
/** 테스트 항목
     * 1. 수신자 email이 정상적으로 반환되는가
     * 2. 수신자 email로 가입된 memberName이 정상적으로 반환되는가
     * (반환값이 정상이다 = 메일 전송이 정상적으로 완료되었다)
     * *//*

    @Test
    @DisplayName("아이디 찾기 테스트 - CASE.성공")
    void vertificationId() {
        // given
        memberService.signUp(
                MemberCreateRequest.builder()
                        .memberName("mafia2")
                        .password("password2!")
                        .passwordConfirm("password2!")
                        .email("mafia2@gmail.com")
                        .build()
        );

        // when
        String result = memberService.vertificationId(
                MemberVertifIdRequest.builder()
                        .email("mafia2@gmail.com")
                        .build()
        );

        // then
        assertEquals("mafia2@gmail.com", result.split("email : ")[1]);
        assertEquals("mafia2", result.split("\\[")[1].split("]")[0]);
    }


    */
/** 테스트 항목
     * 1. 수신자 email이 정상적으로 반환되는가
     * 2. 수신자 email로 가입된 memberName이 정상적으로 반환되는가
     * (반환값이 정상이다 = 메일 전송이 정상적으로 완료되었다)
     * 3. db에 임시 비밀번호가 정상적으로 저장되는가(기존 비밀번호와 다른 값이 저장되어 있는가)
     * *//*

    @Test
    @DisplayName("비밀번호 찾기 테스트 - CASE.성공")
    void vertificationPw() {
        // given
        memberService.signUp(
                MemberCreateRequest.builder()
                        .memberName("mafia2")
                        .password("password2!")
                        .passwordConfirm("password2!")
                        .email("mafia2@gmail.com")
                        .build()
        );

        // when
        String result = memberService.vertificationPw(
                MemberVertifPwRequest.builder()
                        .memberName("mafia2")
                        .email("mafia2@gmail.com")
                        .build()
        );

        // then
        assertEquals("mafia2@gmail.com", result.split("email : ")[1]);
        assertEquals("mafia2", result.split(" ")[2].split(",")[0]);
        assertFalse(passwordEncoder.matches("password2!", result.split(" ")[5].split(",")[0]));
    }


    */
/** 테스트 항목
     * 1. 매번 랜덤 생성이 되는가
     * 2. 생성된 비밀번호의 글자수는 8자인가
     * *//*

    @Test
    @DisplayName("임시 비밀번호 생성 테스트 - CASE.성공")
    void randomPassword() {
        // given - when
        String firstPassword = memberService.randomPassword();
        String secondPassword = memberService.randomPassword();

        // then
        assertNotEquals(firstPassword, secondPassword);
        assertEquals(firstPassword.length(), 8);
        assertEquals(secondPassword.length(), 8);
    }


    */
/** 테스트 항목
     * 1. Dto를 통해 프로필 데이터가 정상적으로 생성되는가
     * *//*

    @Test
    @DisplayName("프로필 생성 테스트 - CASE.성공")
    void createProfile() {
        // given
        MemberProfileCreateRequest memberDto = MemberProfileCreateRequest.builder()
                .nickname("나는야마피아")
                .profileImgNum(1)
                .build();

        // when
        MemberProfileResponse result = memberService.createProfile(memberDto, authHeader);

        // then
        assertEquals(result.getMemberId(), memberJpaRepository.findByMemberName("mafia1").get().getId());
        assertEquals(result.getInGameRole(), GameRole.NONE);
        assertEquals(result.getLevel(), Level.BEGINNER);
        assertEquals(result.getExp(), 0L);
        assertEquals(result.getNickname(), "나는야마피아");
        assertEquals(result.getProfileImg(), ProfileImg.PROFILE01);
        assertFalse(result.isObserver());
    }


    */
/** 테스트 항목
     * 1. Dto를 통해 프로필 데이터가 정상적으로 수정되는가
     * *//*

    @Test
    @DisplayName("프로필 수정 테스트 - CASE.성공")
    void updateProfile() {
        // given
        memberService.createProfile(
                MemberProfileCreateRequest.builder()
                        .nickname("나는야마피아")
                        .profileImgNum(1)
                        .build()
                , authHeader
        );

        MemberProfileUpdateRequest memberDto = MemberProfileUpdateRequest.builder()
                .nickname("나는야시민")
                .profileImgNum(2)
                .build();

        // when
        MemberProfileResponse result = memberService.updateProfile(memberDto, authHeader);

        // then
        assertEquals(result.getMemberId(), memberJpaRepository.findByMemberName("mafia1").get().getId());
        assertEquals(result.getInGameRole(), GameRole.NONE);
        assertEquals(result.getLevel(), Level.BEGINNER);
        assertEquals(result.getExp(), 0L);
        assertEquals(result.getNickname(), "나는야시민");
        assertEquals(result.getProfileImg(), ProfileImg.PROFILE02);
        assertFalse(result.isObserver());
    }


    */
/** 테스트 항목
     * 1. 프로필 데이터가 정상적으로 조회되는가
     * *//*

    @Test
    @DisplayName("프로필 조회 테스트 - CASE.성공")
    void readProfile() {
        // given
        memberService.createProfile(
                MemberProfileCreateRequest.builder()
                        .nickname("나는야마피아")
                        .profileImgNum(1)
                        .build()
                , authHeader
        );

        // when
        MemberProfileRead result = memberService.readProfile(authHeader);

        // then
        assertEquals(result.getNickname(), "나는야마피아");
        assertEquals(result.getProfileImg(), ProfileImg.PROFILE01);
        assertEquals(result.getLevel(), "BEGINNER");
        assertEquals(result.getExp(), 0L);
        assertEquals(result.getNextLevel(), "ASSOCIATE");
        assertEquals(result.getRank(), memberService.rank(1L));
    }


    */
/** 테스트 항목
     * 1. 멤버 게임기록이 정상적으로 조회되는가
     * *//*

    @Test
    @DisplayName("멤버 게임기록 조회 테스트 - CASE.성공")
    void readRecord() {
        // when
        MemberRecordResponse result = memberService.readRecord(authHeader);
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia1");

        // then
        assertEquals(result.getMemberId(), optionalMember.get().getId());
        assertEquals(result.getMafiaCnt() , 0L);
        assertEquals(result.getCitizenCnt() , 0L);
        assertEquals(result.getPoliceCnt() , 0L);
        assertEquals(result.getDoctorCnt() , 0L);
        assertEquals(result.getMafiaWinCnt() , 0L);
        assertEquals(result.getCitizenWinCnt() , 0L);
        assertEquals(result.getTotalCnt() ,0L);
        assertEquals(result.getTotalTime() , 0L);
        assertEquals(result.getKillCnt() , 0L);
        assertEquals(result.getSaveCnt() , 0L);
    }


    */
/** 테스트 항목
     * 1. 멤버 게임기록이 정상적으로 업데이트 되는가
     * *//*

    @Test
    @DisplayName("멤버 게임기록 업데이트 테스트 - CASE.성공")
    void updateRecord() {
        // given
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia1");

        memberGameInfoJpaRepository.save(
                MemberGameInfo.builder()
                        .id(1L).memberId(optionalMember.get().getId()).nickname("나는야마피아").profileImg(ProfileImg.PROFILE01)
                        .level(Level.CAPTAIN).exp(5000L).inGameRole(GameRole.MAFIA).isObserver(false).build());

        gameRecordJpaRepository.save(
                GameRecord.builder()
                        .id(1L).memberId(optionalMember.get().getId()).title("마피아게임하자")
                        .capacity(10L).ownerId(2L).head(8L).state("진행중").duration(60).open(false).winner(true).build());

        // when
        MemberRecordResponse result = memberService.updateRecord(optionalMember.get().getId());

        // then
        assertEquals(result.getMemberId(), optionalMember.get().getId());
        assertEquals(result.getMafiaCnt() , 1L);
        assertEquals(result.getCitizenCnt() , 0L);
        assertEquals(result.getPoliceCnt() , 0L);
        assertEquals(result.getDoctorCnt() , 0L);
        assertEquals(result.getMafiaWinCnt() , 1L);
        assertEquals(result.getCitizenWinCnt() , 0L);
        assertEquals(result.getTotalCnt() ,1L);
        assertEquals(result.getTotalTime() , 60L);
        assertEquals(result.getKillCnt() , 0L);
        assertEquals(result.getSaveCnt() , 0L);
    }

    */
/** 테스트 항목
     * 1. 게임 내 역할이 마피아일 때, 마피아 카운트가 정상적으로 +1 되는가
     * *//*

    @Test
    @DisplayName("역할별 게임 횟수 업데이트 테스트 - CASE.성공")
    void updateInGameRoleCnt() {
        // given
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia1");

        String inGameRole = "mafia";

        MemberRecord memberRecord = MemberRecord.builder().id(1L).memberId(optionalMember.get().getId())
                .mafiaCnt(1L).citizenCnt(0L).policeCnt(0L).doctorCnt(0L).citizenWinCnt(0L).mafiaWinCnt(1L)
                .saveCnt(0L).killCnt(2L).totalCnt(1L).totalTime(30L).build();

        // when
        Map<String, Long> result = memberService.updateInGameRoleCnt(inGameRole, memberRecord);

        // then
        assertEquals(result.get("mafia"), 2L);
    }


    */
/** 테스트 항목
     * 1. 게임 내 역할이 마피아이면서, 게임에서 마피아팀이 이겼을 때, 마피아팀 승리 카운트가 정상적으로 +1 되는가
     * *//*

    @Test
    @DisplayName("시민팀/마피아팀으로 이긴 횟수 업데이트 테스트 - CASE.성공")
    void updateWinCnt() {
        // given
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia1");

        String inGameRole = "mafia";

        MemberRecord memberRecord = MemberRecord.builder().id(1L).memberId(optionalMember.get().getId())
                .mafiaCnt(1L).citizenCnt(0L).policeCnt(0L).doctorCnt(0L).citizenWinCnt(0L).mafiaWinCnt(1L)
                .saveCnt(0L).killCnt(2L).totalCnt(1L).totalTime(30L).build();

        GameRecord gameRecord = gameRecordJpaRepository.save(
                GameRecord.builder().id(1L).title("마피아게임방1").memberId(1L).ownerId(1L).capacity(10L)
                        .head(8L).open(true).state("진행중").duration(60).winner(true).build()
        );

        // when
        Map<String, Long> result = memberService.updateWinCnt(inGameRole, memberRecord, gameRecord);

        // then
        assertEquals(result.get("mafiaWinCnt"), 2L);
    }


        */
/** 테스트 항목
         * 1. 총 게임 횟수가 정상적으로 +1 되는가
         * *//*

        @Test
        @DisplayName(("총 게임 횟수 업데이트 테스트 - CASE.성공"))
        void updateToalCnt() {
            Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia1");
            // given
            MemberRecord memberRecord = MemberRecord.builder().id(1L).memberId(optionalMember.get().getId())
                    .mafiaCnt(1L).citizenCnt(0L).policeCnt(0L).doctorCnt(0L).citizenWinCnt(0L).mafiaWinCnt(1L)
                    .saveCnt(0L).killCnt(2L).totalCnt(1L).totalTime(30L).build();

            // when
            Map<String, Long> result = memberService.updateTotalCnt(memberRecord);

            // then
            assertEquals(result.get("totalCnt"), 2L);
        }


    */
/** 테스트 항목
     * 1. 총 게임 시간이 게임 진행 시간만큼 정상적으로 증가하는가
     * *//*

    @Test
    @DisplayName(("총 게임 시간 업데이트 테스트 - CASE.성공"))
    void updateTotalTime() {
        // given
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia1");

        MemberRecord memberRecord = MemberRecord.builder().id(1L).memberId(optionalMember.get().getId())
                .mafiaCnt(1L).citizenCnt(0L).policeCnt(0L).doctorCnt(0L).citizenWinCnt(0L).mafiaWinCnt(1L)
                .saveCnt(0L).killCnt(2L).totalCnt(1L).totalTime(30L).build();

        GameRecord gameRecord = gameRecordJpaRepository.save(
                GameRecord.builder().id(1L).title("마피아게임방1").memberId(1L).ownerId(1L).capacity(10L)
                        .head(8L).open(true).state("진행중").duration(60).winner(true).build());

        // when
        Map<String, Long> result = memberService.updateTotalTime(memberRecord, gameRecord);

        // then
        assertEquals(result.get("totalTime"), 90L);
    }


    */
/** 테스트 항목
     * 1. 멤버 기록이 정상적으로 생성되는가
     * *//*

    @Test
    @DisplayName("멤버 기록 생성(초기화) 테스트 - CASE.성공")
    void newMemberRecord() {
        // given
        memberJpaRepository.save(
                Member.builder().id(2L).memberName("mafia2").password("password1!").email("mafia2@gmail.com").role("USER").isDeleted(false).build()
        );

        // when
        MemberRecord result = memberService.newMemberRecord("mafia2");

        // then
        assertEquals(result.getMafiaCnt() , 0L);
        assertEquals(result.getCitizenCnt() , 0L);
        assertEquals(result.getPoliceCnt() , 0L);
        assertEquals(result.getDoctorCnt() , 0L);
        assertEquals(result.getMafiaWinCnt() , 0L);
        assertEquals(result.getCitizenWinCnt() , 0L);
        assertEquals(result.getTotalCnt() ,0L);
        assertEquals(result.getTotalTime() , 0L);
        assertEquals(result.getKillCnt() , 0L);
        assertEquals(result.getSaveCnt() , 0L);
    }


    */
/** 테스트 항목
     * 1. 회원가입 시 정상적으로 멤버 기록이 생성되는가
     * *//*

    @Test
    @DisplayName("회원가입 시 멤버 기록 생성 테스트 - CASE.성공")
    void createRecord() {
        // when
        memberService.signUp(
                MemberCreateRequest.builder().memberName("mafia2").password("password1!").passwordConfirm("password1!").email("mafia2@gmail.com").build()
        );

        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia2");
        Optional<MemberRecord> result = memberRecordJpaRepository.findByMemberId(optionalMember.get().getId());

        // then
        assertEquals(result.get().getMafiaCnt() , 0L);
        assertEquals(result.get().getCitizenCnt() , 0L);
        assertEquals(result.get().getPoliceCnt() , 0L);
        assertEquals(result.get().getDoctorCnt() , 0L);
        assertEquals(result.get().getMafiaWinCnt() , 0L);
        assertEquals(result.get().getCitizenWinCnt() , 0L);
        assertEquals(result.get().getTotalCnt() ,0L);
        assertEquals(result.get().getTotalTime() , 0L);
        assertEquals(result.get().getKillCnt() , 0L);
        assertEquals(result.get().getSaveCnt() , 0L);
    }
}*/
