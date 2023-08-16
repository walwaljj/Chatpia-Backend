package com.springles.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
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
    private String email;

    // 게임 아이디
    @Column(nullable = false, unique = true)
    private String memberName;

    // 비밃번호
    @Column(nullable = false)
    private String password;

    // 유저 역할 - Admin(관리자), User(일반유저)
    @Column(nullable = false)
    private String role;

    // 탈퇴 여부
    @Column(nullable = false)
    private Boolean isDeleted;

    @Override
    public String toString() {
        return "id : " + id
                + ", memberName : " + memberName
                + ", password : " + password
                + ", email : " + email
                + ", role : " + role
                + ", isDeleted : " + isDeleted;
    }
}