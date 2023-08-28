package com.springles.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class GameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_record_id")
    private Long id; // 게임 ID

    @Column(nullable = false)
    private String title; // 게임방 제목

    @Column(nullable = false)
    private Long ownerId; // 방장 ID

    // enum으로 수정할 것
    @Column(nullable = false)
    private String state; // 게임방 상태(게임 진행중, 게임 참여 가능)

    @Column(nullable = false)
    private Long capacity; // 정원

    @Column(nullable = false)
    private Long head; // 참여자 수

    @Column(nullable = false)
    private boolean open; // 공개방 / 비밀방

    @Column(nullable = false)
    private boolean winner; // 이긴팀 (true: 마피아, false: 시민)?

    @Column(nullable = false)
    private LocalDateTime duration; // 게임 진행 시간

    // entity 맵핑 필요
    private Long memberId;
}
