package com.springles.repository;

import com.springles.domain.entity.MemberGameInfo;
import com.springles.domain.entity.MemberRecord;
import com.springles.repository.support.MemberGameInfoJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRecordJpaRepositoryTest {

    @Autowired
    MemberRecordJpaRepository memberRecordJpaRepository;

    @AfterEach
    public void deleteAll(){
        memberRecordJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("memberId에 해당하는 memberRecord 반환 테스트 - CASE.성공")
    void findByMemberId() {
        // given
        memberRecordJpaRepository.save(
                MemberRecord.builder().id(1L).memberId(1L).mafiaCnt(1L).citizenCnt(0L).policeCnt(0L).doctorCnt(0L).citizenWinCnt(0L).mafiaWinCnt(1L).saveCnt(0L).killCnt(2L).totalCnt(1L).totalTime(30L).build()
        );

        // when
        Optional<MemberRecord> result = memberRecordJpaRepository.findByMemberId(1L);

        // then
        assertEquals(result.get().getMafiaCnt(), 1L);
        assertEquals(result.get().getCitizenCnt(), 0L);
        assertEquals(result.get().getPoliceCnt(), 0L);
        assertEquals(result.get().getDoctorCnt(), 0L);
        assertEquals(result.get().getCitizenWinCnt(), 0L);
        assertEquals(result.get().getMafiaWinCnt(), 1L);
        assertEquals(result.get().getSaveCnt(), 0L);
        assertEquals(result.get().getKillCnt(), 2L);
        assertEquals(result.get().getTotalCnt(), 1L);
        assertEquals(result.get().getTotalTime(), 30L);
    }
}