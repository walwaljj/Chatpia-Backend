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

    /** 역할별 게임 횟수 */
    private Long mafiaCnt;

    private Long citizenCnt;

    private Long doctorCnt;

    private Long policeCnt;

    /** 의사, 마피아로써 살리고 죽인 횟수 */
    private Long saveCnt;

    private Long killCnt;

    /** 역할별 이긴 횟수 */
    private Long mafiaWinCnt;

    private Long citizenWinCnt;

    /** 역할별 승률 */
    private String mafiaOdds;

    private String citizenOdds;

    private String policeOdds;

    private String doctorOdds;

    /** 전체 게임 횟수 */
    private Long totalCnt;

    /** 전체 게임 시간 */
    private Long totalTime;

    public static MemberRecordResponse of(MemberRecord memberRecord) {

        float mafiaOddsOp = (memberRecord.getMafiaWinCnt() != 0L) && (memberRecord.getMafiaCnt() != 0L) ? (memberRecord.getMafiaWinCnt() / (float)memberRecord.getMafiaCnt() * 100) : 0;
        float citizenOddsOp = (memberRecord.getCitizenWinCnt() != 0L) && (memberRecord.getCitizenCnt() != 0L) ? (memberRecord.getCitizenWinCnt() / (float)memberRecord.getCitizenCnt() * 100) : 0;
        float policeOddsOp = (memberRecord.getPoliceWinCnt() != 0L) && (memberRecord.getPoliceCnt() != 0L) ? (memberRecord.getPoliceWinCnt() / (float)memberRecord.getPoliceCnt() * 100) : 0;
        float doctorOddsOp = (memberRecord.getDoctorWinCnt() != 0L) && (memberRecord.getDoctorCnt() != 0L) ? (memberRecord.getDoctorWinCnt() / (float)memberRecord.getDoctorCnt() * 100) : 0;

        String mafiaOdds = String.format("%.1f", mafiaOddsOp);
        String citizenOdds = String.format("%.1f", citizenOddsOp);
        String policeOdds = String.format("%.1f", policeOddsOp);
        String doctorOdds = String.format("%.1f", doctorOddsOp);

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
