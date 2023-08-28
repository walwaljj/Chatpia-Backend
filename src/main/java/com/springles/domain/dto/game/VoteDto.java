//package com.springles.domain.dto.game;
//
//import com.springles.domain.entity.Vote;
//import lombok.Builder;
//import lombok.Getter;
//import org.springframework.data.redis.core.index.Indexed;
//
//@Builder
//@Getter
//public class VoteDto {
//    private Long voteId;
//    private Long roomId; // 방 넘버로 조회 가능
//    private Long stageId; // 한 게임 안에서 몇 번째 투표인지로 조회 가능
//    private Long voter; // 투표를 한 사람 PlayerID
//    private Long election; // 투표를 당한 사람 PlaterID
//    private String voteCase;
//
//    public static VoteDto fromEntity(Vote vote) {
//        return VoteDto.builder()
//                .roomId(vote.getRoomId())
//                .stageId(vote.getStageId())
//                .voter(vote.getVoter())
//                .election(vote.getElection())
//                .voteCase(vote.getVoteCase()).build();
//    }
//
//    public static Vote createVote(VoteDto voteDto) {
//        return Vote.builder()
//                .roomId(voteDto.getRoomId())
//                .stageId(voteDto.getStageId())
//                .voter(voteDto.getVoter())
//                .election(voteDto.getElection())
//                .voteCase(voteDto.getVoteCase())
//                .build();
//    }
//}
