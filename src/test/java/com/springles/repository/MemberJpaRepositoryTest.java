/*
package com.springles.repository;

import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.entity.Member;
import com.springles.exception.CustomException;
import com.springles.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    public void deleteAll(){
        memberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("멤버 이름 조회 테스트 ( 대 소문자 구분 하지 않음 )")
    public void findByOpenTrueTest() {

        // given
        for (int i = 1; i <= 2; i++) {
            memberService.signUp(MemberCreateRequest.builder()
                    .memberName("testUser" + i)
                    .password("1")
                    .passwordConfirm("1")
                    .email("1@")
                    .role("user")
                    .isDeleted(false).build());
        }

        //when
        Optional<List<Member>> findByNameIgnoreCase = memberJpaRepository.findAllByMemberNameContainingIgnoreCase("uSeR");

        //then
        List<Member> members = findByNameIgnoreCase.get();
        for (int i = 0, j = 1; i < members.size(); i++, j++) {
            assertThat(members.get(i).getMemberName()).isEqualTo("testUser" + j);
        }
    }


    @Test
    @DisplayName("memberName에 해당하는 Member 존재여부 확인 테스트 - CASE.성공")
    void existsByMemberName() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("mafia1")
                .password(passwordEncoder.encode("password1!"))
                .email("mafia1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        boolean result = memberJpaRepository.existsByMemberName("mafia1");

        // then
        assertTrue(result);
    }


    @Test
    @DisplayName("memberName에 해당하는 Member 호출 테스트 - CASE.성공")
    void findByMemberName() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("mafia1")
                .password(passwordEncoder.encode("password1!"))
                .email("mafia1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia1");

        // then
        assertNotNull(optionalMember.get().getId());
        assertEquals(optionalMember.get().getMemberName(), "mafia1");
        assertTrue(passwordEncoder.matches("password1!", optionalMember.get().getPassword()));
        assertEquals(optionalMember.get().getRole(), "USER");
        assertFalse(optionalMember.get().getIsDeleted());
        assertEquals(optionalMember.get().getEmail(), "mafia1@gmail.com");
    }


    @Test
    @DisplayName("memberName에 해당하는 Member 삭제 테스트(삭제: isDeleted == true) - CASE.성공")
    void deleteByMemberName() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("mafia1")
                .password(passwordEncoder.encode("password1!"))
                .email("mafia1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        memberJpaRepository.deleteByMemberName("mafia1");
        Optional<Member> optionalMember = memberJpaRepository.findByMemberName("mafia1");

        // then
        assertTrue(optionalMember.get().getIsDeleted());
    }


    @Test
    @DisplayName("email에 해당하는 Member 전체 호출 테스트 - CASE.성공")
    void findAllByEmail() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("mafia1")
                .password(passwordEncoder.encode("password1!"))
                .email("mafia@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        memberJpaRepository.save(Member.builder()
                .memberName("mafia2")
                .password(passwordEncoder.encode("password1!"))
                .email("mafia@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        List<Member> memberList = memberJpaRepository.findAllByEmail("mafia@gmail.com");

        // then
        assertEquals(memberList.size(), 2);
    }


    @Test
    @DisplayName("email과 memberName에 해당하는 Member 호출 테스트 - CASE.성공")
    void findByMemberNameAndEmail() {
        // given
        memberJpaRepository.save(Member.builder()
                .memberName("mafia1")
                .password(passwordEncoder.encode("password1!"))
                .email("mafia1@gmail.com")
                .role("USER")
                .isDeleted(false)
                .build());

        // when
        Optional<Member> optionalMember = memberJpaRepository.findByMemberNameAndEmail("mafia1", "mafia1@gmail.com");

        // then
        assertNotNull(optionalMember.get().getId());
        assertEquals(optionalMember.get().getMemberName(), "mafia1");
        assertTrue(passwordEncoder.matches("password1!", optionalMember.get().getPassword()));
        assertEquals(optionalMember.get().getRole(), "USER");
        assertFalse(optionalMember.get().getIsDeleted());
        assertEquals(optionalMember.get().getEmail(), "mafia1@gmail.com");
    }
}
*/
