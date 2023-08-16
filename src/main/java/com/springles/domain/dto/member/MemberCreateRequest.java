package com.springles.domain.dto.member;

import com.springles.domain.entity.Member;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
@Getter
public class MemberCreateRequest {

    // 게임 아이디
    @NotBlank(message = "아이디를 입력해주세요.")
    @Column(unique = true)
    @Size(min = 6, max = 20, message = "아이디는 6 ~ 20자 사이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문 대소문자, 숫자만 입력 할 수 있습니다.")
    private String memberName;

    // 비밃번호
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, message = "비밀번호는 6자 이상이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]+$", message = "비밀번호는 영문 대소문자, 숫자, 특수문자(!,@,#,$,%,^,&,*)만 입력할 수 있습니다.")
    private String password;

    // 비밃번호 확인
    private String passwordConfirm;

    // 이메일
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    // 유저 역할 - Admin(관리자), User(일반유저)
    private String role;

    // 탈퇴 여부
    private Boolean isDeleted;

    public static MemberCreateRequest fromEntity(Member entity) {
        return MemberCreateRequest.builder()
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

    @Override
    public String toString() {
        return  ", memberName : " + memberName
                + ", password : " + password
                + ", email : " + email
                + ", role : " + role
                + ", isDeleted : " + isDeleted;
    }
}