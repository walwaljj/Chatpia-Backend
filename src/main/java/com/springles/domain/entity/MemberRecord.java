package com.springles.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_record_id")
    private Long id;

    @Column(nullable = false)
    private Long mafiaCnt;

    @Column(nullable = false)
    private Long citizenCnt;

    @Column(nullable = false)
    private Long doctorCnt;

    @Column(nullable = false)
    private Long policeCnt;

    @Column(nullable = false)
    private Long saveCnt;

    @Column(nullable = false)
    private Long killCnt;

    @Column(nullable = false)
    private Long mafiaWinCnt;

    @Column(nullable = false)
    private Long citizenWinCnt;

    @Column(nullable = false)
    private Long policeWinCnt;

    @Column(nullable = false)
    private Long doctorWinCnt;

    @Column(nullable = false)
    private Long totalCnt;

    @Column(nullable = false)
    private Long totalTime;

    // entity 맵핑 필요
    private Long memberId;

}