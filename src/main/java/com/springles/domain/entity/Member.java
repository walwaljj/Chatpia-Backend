package com.springles.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    // 이메일
    @Column(nullable = false)
    private String email;

    // 게임 아이디
    @Column(nullable = false, unique = true)
    private String memberName;

    // 비밃번호
    @Column(nullable = false)
    private String password;

    // 유저 역할 (Admin, User) 관리자와 일반유저
    @Column(nullable = false)
    private String role;

    // 삭제 여부
    @Column(nullable = false)
    private Boolean isDeleted;
}
