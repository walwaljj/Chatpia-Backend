//package com.springles.repository;
//
//import com.springles.domain.entity.GameRecord;
//import com.springles.domain.entity.Member;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class GameRecordJpaRepositoryTest {
//
//    @Autowired
//    MemberJpaRepository memberJpaRepository;
//    @Autowired
//    GameRecordJpaRepository gameRecordJpaRepository;
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @AfterEach
//    public void deleteAll(){
//        gameRecordJpaRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("게임 기록 중 가장 최근에 한 게임 반환 테스트")
//    void findTOP1ByMemberIdOrderByIdDesc() {
//        // given
//        // Member 생성
//        Member member1 = Member.builder()
//                .memberName("mafia1").password(passwordEncoder.encode("password1!"))
//                .email("mafia@gmail.com").role("USER").isDeleted(false).build();
//
//        Member member2 = Member.builder()
//                .memberName("mafia2").password(passwordEncoder.encode("password1!"))
//                .email("mafia@gmail.com").role("USER").isDeleted(false).build();
//
//        Member member3 = Member.builder()
//                .memberName("mafia2").password(passwordEncoder.encode("password1!"))
//                .email("mafia@gmail.com").role("USER").isDeleted(false).build();
//
//        memberJpaRepository.save(member1);
//        memberJpaRepository.save(member2);
//        memberJpaRepository.save(member3);
//
//        List<Member> memberList = new ArrayList<>();
//        memberList.add(member1);
//        memberList.add(member2);
//        memberList.add(member3);
//
//        for(Long i = 1L; i <= 3L; i++) {
//            gameRecordJpaRepository.save(
//                    GameRecord.builder().id(i).title("마피아게임방" + i).ownerId(1L).capacity(10L).head(8L).open(true).state("진행중").duration(60).winner(true).build()
//            );
//        }
//
//        // when
//        GameRecord result = gameRecordJpaRepository.findTOP1MemberIdOrderByIdDesc(1L);
//
//        // then
//        assertEquals(result.getId(), 3L);
//        assertEquals(result.getTitle(), "마피아게임방3");
//        assertEquals(result.getOwnerId(), 1L);
//        assertEquals(result.getCapacity(), 10L);
//        assertEquals(result.getHead(), 8L);
//        assertTrue(result.isOpen());
//        assertEquals(result.getState(), "진행중");
//        assertEquals(result.getDuration(), 60L);
//        assertTrue(result.isWinner());
//    }
//}