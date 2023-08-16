package com.springles.domain.dto.member;

import com.springles.domain.entity.Member;

import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;

@Builder
public class MemberDetails implements UserDetails {

    // 게임 아이디
    private String memberName;

    // 비밃번호
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

    public static MemberDetails fromEntity(Member entity) {
        return MemberDetails.builder()
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
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.memberName;
    }

    @Override
    // 계정이 갖고 있는 권한 목록 return
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    @Override
    // 계정 만료 여부 체크 (true = 만료되지 않음)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    // 계정 잠김 여부 체크 (true = 잠기지 않음)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    // 계정의 패스워드 만료 여부 체크 (true = 만료되지 않음)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // 사용 가능한 계정인지 체크 (true = 사용 가능)
    public boolean isEnabled() {
        return true;
    }
}