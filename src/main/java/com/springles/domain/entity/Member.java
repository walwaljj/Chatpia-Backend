package com.springles.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
@SQLDelete(sql = "UPDATE member SET is_deleted = true WHERE member_id = ?")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    // 게임 아이디
    private String memberName;

    // 비밃번호
    private String password;

    // 이메일
    private String email;

    // 유저 역할 - Admin(관리자), User(일반유저)
    @NotNull
    private String role;

    // 탈퇴 여부
    @NotNull
    private Boolean isDeleted;

    @Override
    public String toString() {
        return  ", memberName : " + memberName
                + ", password : " + password
                + ", email : " + email
                + ", role : " + role
                + ", isDeleted : " + isDeleted;
    }
}