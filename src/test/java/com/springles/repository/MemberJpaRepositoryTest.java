package com.springles.repository;

import com.springles.domain.entity.Member;
import com.springles.exception.CustomException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("memberName에 해당하는 Member 존재여부 확인 테스트")
    void existsByMemberName() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("user1")
                .password(passwordEncoder.encode("password1!"))
                .email("user1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        boolean result = memberJpaRepository.existsByMemberName("user1");

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("memberName에 해당하는 Member 호출 테스트")
    void findByMemberName() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("user1")
                .password(passwordEncoder.encode("password1!"))
                .email("user1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("user1");

        // then
        assertNotNull(optionalMember.get().getId());
        assertEquals(optionalMember.get().getMemberName(), "user1");
        assertTrue(passwordEncoder.matches("password1!", optionalMember.get().getPassword()));
        assertEquals(optionalMember.get().getRole(), "USER");
        assertFalse(optionalMember.get().getIsDeleted());
        assertEquals(optionalMember.get().getEmail(), "user1@gmail.com");
    }

    @Test
    @DisplayName("memberName에 해당하는 Member 삭제 테스트(삭제: isDeleted == true)")
    void deleteByMemberName() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("user1")
                .password(passwordEncoder.encode("password1!"))
                .email("user1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        memberJpaRepository.deleteByMemberName("user1");
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("user1");

        // then
        assertTrue(optionalMember.get().getIsDeleted());
    }

    @Test
    @DisplayName("email에 해당하는 Member 전체 호출 테스트")
    void findAllByEmail() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("user1")
                .password(passwordEncoder.encode("password1!"))
                .email("user@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        memberJpaRepository.save(Member.builder()
                .memberName("user2")
                .password(passwordEncoder.encode("password1!"))
                .email("user@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        List<Member> memberList = memberJpaRepository.findAllByEmail("user@gmail.com");

        // then
        assertEquals(memberList.size(), 2);
    }

    @Test
    @DisplayName("email과 memberName에 해당하는 Member 호출 테스트")
    void findByMemberNameAndEmail() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("user1")
                .password(passwordEncoder.encode("password1!"))
                .email("user1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        Optional<Member> optionalMember = memberJpaRepository.findByMemberNameAndEmail("user1", "user1@gmail.com");

        // then
        assertNotNull(optionalMember.get().getId());
        assertEquals(optionalMember.get().getMemberName(), "user1");
        assertTrue(passwordEncoder.matches("password1!", optionalMember.get().getPassword()));
        assertEquals(optionalMember.get().getRole(), "USER");
        assertFalse(optionalMember.get().getIsDeleted());
        assertEquals(optionalMember.get().getEmail(), "user1@gmail.com");
    }
}
