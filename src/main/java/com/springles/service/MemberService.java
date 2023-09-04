package com.springles.service;

import com.springles.domain.constants.Level;
import com.springles.domain.dto.member.*;
import com.springles.domain.entity.GameRecord;
import com.springles.domain.entity.MemberRecord;

import java.io.IOException;
import java.util.Map;

public interface MemberService {

    // 사용자 정보 API
    MemberInfoResponse getUserInfo(String authHeader);

    String signUp(MemberCreateRequest memberDto);

    String updateInfo(MemberUpdateRequest memberDto, String authHeader);

    void signOut(MemberDeleteRequest memberDto, String accessToken);

    MemberLoginResponse login(MemberLoginRequest memberDto);

    void logout(String authHeader);

    String vertificationId(MemberVertifIdRequest memberDto);

    String vertificationPw(MemberVertifPwRequest memberDto);

    String randomPassword();

    boolean memberExists(String memberName);

    MemberProfileResponse createProfile(MemberProfileCreateRequest memberDto, String accessToken);

    MemberProfileResponse updateProfile(MemberProfileUpdateRequest memberDto, String accessToken);

    MemberProfileRead readProfile(String accessToken);

    MemberProfileResponse levelUp(Long memberId);

    Level nextLevel(Level rawLevel);

    Long rank(Long memberId);

    /** 멤버 게임 기록 조회 */
    MemberRecordResponse readRecord(String accessToken);

    /** 멤버 게임 기록 update */
    MemberRecordResponse updateRecord(Long memberId);

    /** 역할별 게임 횟수 update */
    Map<String, Long> updateInGameRoleCnt(String inGameRole, MemberRecord memberRecord);

    /** 시민/마피아로 이긴 팀 횟수 update */
    Map<String, Long> updateWinCnt(String inGameRole, MemberRecord memberRecord, GameRecord gameRecord);

    /** 총 게임 횟수 update */
    Map<String, Long> updateTotalCnt(MemberRecord memberRecord);

    /** 총 게임 시간 update */
    Map<String, Long> updateTotalTime(MemberRecord memberRecord, GameRecord gameRecord);

    /** 멤버 기록 생성(초기화) */
    MemberRecord newMemberRecord(String memberName);
}
