package com.springles.repository;

import com.springles.domain.entity.Member;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    void init() {
        Member.builder()
                .memberName("user1")
                .password("password1!")
                .email("user1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build();

        Member.builder()
                .memberName("user2")
                .password("password1!")
                .email("user2@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build();

        Member.builder()
                .memberName("user3")
                .password("password1!")
                .email("user3@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build();
    }

    @Test
    @DisplayName("memberName에 해당하는 회원 존재여부 확인")
    void existsByMemberName() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("user1")
                .password("password1!")
                .email("user1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        boolean result = memberJpaRepository.existsByMemberName("user1");

        // then
        assertEquals(result, true);
    }

    @Test
    @DisplayName("memberName에 해당하는 회원 entity 호출")
    void findByMemberName() {
    }

    @Test
    @DisplayName("memberName에 해당하는 회원 삭제(softDelete)")
    void deleteByMemberName() {
    }

    @Test
    @DisplayName("email에 해당하는 memberName 전체 호출")
    void findAllByEmail() {
    }

    @Test
    @DisplayName("email과 memberName에 해당하는 회원 entity 호출")
    void findByMemberNameAndEmail() {
    }
}