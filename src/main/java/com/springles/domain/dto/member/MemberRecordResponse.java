package com.springles.domain.dto.member;

import com.springles.domain.entity.MemberRecord;
import lombok.Builder;
import lombok.Getter;

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

    public static MemberRecordResponse of(MemberRecord memberRecord) {
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
                .build();
    }
}
