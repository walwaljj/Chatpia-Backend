package com.springles.service;

import com.springles.domain.dto.member.*;

import java.io.IOException;

public interface MemberService {

    String signUp(MemberCreateRequest memberDto);

    String updateInfo(MemberUpdateRequest memberDto, String authHeader);

    void signOut(MemberDeleteRequest memberDto, String authHeader);

    boolean memberExists(String memberName);

    String login(MemberLoginRequest memberDto);

    void logout(String authHeader);

    String vertificationId(MemberVertifIdRequest memberDto);
}
