package com.springles.repository;

import com.springles.domain.entity.GameRecord;
import com.springles.domain.entity.Member;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GameRecordJpaRepositoryTest {

    @Autowired
    GameRecordJpaRepository gameRecordJpaRepository;

    @AfterEach
    public void deleteAll(){
        gameRecordJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("게임 기록 중 가장 최근에 한 게임 반환 테스트")
    void findTOP1ByMemberIdOrderByIdDesc() {
        // given
        for(Long i = 1L; i <= 3L; i++) {
            gameRecordJpaRepository.save(
                    GameRecord.builder().id(i).title("마피아게임방" + i).memberId(1L).ownerId(1L).capacity(10L).head(8L).open(true).state("진행중").duration(60).winner(true).build()
            );
        }

        // when
        GameRecord result = gameRecordJpaRepository.findTOP1ByMemberIdOrderByIdDesc(1L);

        // then
        assertEquals(result.getId(), 3L);
        assertEquals(result.getTitle(), "마피아게임방3");
        assertEquals(result.getMemberId(), 1L);
        assertEquals(result.getOwnerId(), 1L);
        assertEquals(result.getCapacity(), 10L);
        assertEquals(result.getHead(), 8L);
        assertTrue(result.isOpen());
        assertEquals(result.getState(), "진행중");
        assertEquals(result.getDuration(), 60L);
        assertTrue(result.isWinner());
    }
}