package com.springles.service;

import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.dto.member.MemberDeleteRequest;
import com.springles.domain.dto.member.MemberLoginRequest;
import com.springles.domain.dto.member.MemberUpdateRequest;
import com.springles.domain.entity.Member;

public interface MemberService {

    String signUp(MemberCreateRequest memberDto);

    String updateInfo(MemberUpdateRequest memberDto, String authHeader);

    void signOut(MemberDeleteRequest memberDto, String authHeader);

    boolean memberExists(String memberName);

    String login(MemberLoginRequest memberDto);
}
