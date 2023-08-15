package com.springles.domain.dto.member;

import com.springles.domain.entity.Member;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
public class MemberResponse {

    // 게임 아이디
    @Getter
    private String memberName;

    // 비밃번호
    @Getter
    private String password;

    // 비밃번호 확인
    @Getter
    private String passwordConfirm;

    // 이메일
    @Getter
    private String email;

    // 유저 역할 - Admin(관리자), User(일반유저)
    @Getter
    private String role;

    // 탈퇴 여부
    @Getter
    private Boolean isDeleted;

    public static MemberResponse fromEntity(Member entity) {
        return MemberResponse.builder()
                .memberName(entity.getMemberName())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .role(entity.getRole())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    public Member newMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .memberName(memberName)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role("USER")
                .isDeleted(false)
                .build();
    }
}