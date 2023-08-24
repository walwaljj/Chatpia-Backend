package com.springles.repository;

import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.entity.Member;
import com.springles.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberJpaRepository memberRepository;
    @Autowired
    MemberService memberService;

    @AfterEach
    public void deleteAll(){
        memberRepository.deleteAll();
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
        Optional<List<Member>> findByNameIgnoreCase = memberRepository.findAllByMemberNameContainingIgnoreCase("uSeR");

        //then
        List<Member> members = findByNameIgnoreCase.get();
        for (int i = 0, j = 1; i < members.size(); i++, j++) {
            assertThat(members.get(i).getMemberName()).isEqualTo("testUser" + j);
        }

    }
}