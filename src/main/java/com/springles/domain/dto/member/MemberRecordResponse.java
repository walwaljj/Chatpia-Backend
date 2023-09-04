package com.springles.domain.dto.member;

import com.springles.domain.entity.MemberRecord;
import lombok.Builder;
import lombok.Getter;

import java.rmi.UnexpectedException;

@Getter
@Builder
public class MemberRecordResponse {

    private Long id;

    private Long memberId;

    private Long mafiaCnt;

    private Long citizenCnt;

    private Long doctorCnt;

    private Long policeCnt;

    private Long saveCnt;

    private Long killCnt;

    private Long mafiaWinCnt;

    private Long citizenWinCnt;

    private Long totalCnt;

    private Long totalTime;

    private Long mafiaOdds;

    private Long citizenOdds;

    private Long policeOdds;

    private Long doctorOdds;

    public static MemberRecordResponse of(MemberRecord memberRecord) {

        Long mafiaOdds = (memberRecord.getMafiaWinCnt() != 0L) && (memberRecord.getMafiaCnt() != 0L) ? (memberRecord.getMafiaWinCnt() / memberRecord.getMafiaCnt() * 100) : 0;
        Long citizenOdds = (memberRecord.getCitizenWinCnt() != 0L) && (memberRecord.getCitizenCnt() != 0L) ? (memberRecord.getCitizenWinCnt() / memberRecord.getCitizenCnt() * 100) : 0;
        Long policeOdds = (memberRecord.getPoliceWinCnt() != 0L) && (memberRecord.getPoliceCnt() != 0L) ? (memberRecord.getPoliceWinCnt() / memberRecord.getPoliceCnt() * 100) : 0;
        Long doctorOdds = (memberRecord.getDoctorWinCnt() != 0L) && (memberRecord.getDoctorCnt() != 0L) ? (memberRecord.getDoctorWinCnt() / memberRecord.getDoctorCnt() * 100) : 0;


        return MemberRecordResponse.builder()
                .id(memberRecord.getId())
                .memberId(memberRecord.getMemberId())
                .mafiaCnt(memberRecord.getMafiaCnt())
                .citizenCnt(memberRecord.getCitizenCnt())
                .policeCnt(memberRecord.getPoliceCnt())
                .doctorCnt(memberRecord.getDoctorCnt())
                .citizenWinCnt(memberRecord.getCitizenWinCnt())
                .mafiaWinCnt(memberRecord.getMafiaWinCnt())
                .saveCnt(memberRecord.getSaveCnt())
                .killCnt(memberRecord.getKillCnt())
                .totalCnt(memberRecord.getTotalCnt())
                .totalTime(memberRecord.getTotalTime())
                .mafiaOdds(mafiaOdds)
                .citizenOdds(citizenOdds)
                .policeOdds(policeOdds)
                .doctorOdds(doctorOdds)
                .build();
    }
}
