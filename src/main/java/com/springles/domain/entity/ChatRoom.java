package com.springles.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;    // 채팅방 id

    @Column(nullable = false)
    private String title;   // 채팅방 제목

    @Column(nullable = false)
    private String password;   // 채팅방 PW

    @Column(nullable = false)
    private String state;   // 채팅방 상태

    @Column(nullable = false)
    private Long capacity;  // 정원

    @Column(nullable = false)
    private Long head;  // 참여자 수

    @Column(nullable = false)
    private boolean open;   // 공개방/비밀방
}