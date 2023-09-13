package com.springles.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(nullable = false)
    private Long capacity; // 정원

    @Column(nullable = false)
    private Long head; // 참여자 수

    @Column(nullable = false)
    private boolean open; // 공개방 / 비밀방

    @Column(nullable = false)
    private boolean winner; // 이긴팀 (true: 마피아, false: 시민)?

    @Column(nullable = false)
    private int duration; // 게임 진행 시간

    @ManyToMany
    @JoinTable(
            name = "game_member_mapping_table",
            joinColumns = @JoinColumn(name = "game_record_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private final List<Member> memberList = new ArrayList<>();

    public void addMember(Member member) {
        memberList.add(member);
    }
}
