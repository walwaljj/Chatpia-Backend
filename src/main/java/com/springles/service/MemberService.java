package com.springles.service;

import com.springles.domain.dto.member.*;

import java.io.IOException;

public interface MemberService {

    // 사용자 정보 API
    MemberInfoResponse getUserInfo(String authHeader);

    String signUp(MemberCreateRequest memberDto);

    String updateInfo(MemberUpdateRequest memberDto, String authHeader);

    void signOut(MemberDeleteRequest memberDto, String authHeader);

    MemberLoginResponse login(MemberLoginRequest memberDto);

    void logout(String authHeader);

    String vertificationId(MemberVertifIdRequest memberDto);

    String vertificationPw(MemberVertifPwRequest memberDto);

    String randomPassword();

    boolean memberExists(String memberName);

    MemberProfileResponse createProfile(MemberProfileCreateRequest memberDto, String authHeader);

    MemberProfileResponse updateProfile(MemberProfileUpdateRequest memberDto, String authHeader);

    MemberProfileRead readProfile(String authHeader);

    MemberProfileResponse levelUp(String authHeader);
}
