package com.springles.domain.constants;

import com.springles.domain.dto.response.ResResult;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
public enum ResponseCode {

    /* CHATROOM */
    CHATROOM_CREATE(HttpStatus.CREATED, "201", "채팅방 생성 성공"),
    CHATROOM_UPDATE(HttpStatus.CREATED, "201", "채팅방 수정 성공"),
    CHATROOM_DELETE(HttpStatus.NO_CONTENT, "201", "채팅방 삭제 성공"),
    CHATROOM_SEARCH(HttpStatus.OK,"200", "채팅방 검색 완료"),

    /* AUTH */
    MEMBER_SAVE(HttpStatus.CREATED, "201", "회원가입 성공"),
    MEMBER_LOGIN(HttpStatus.OK, "200", "로그인 성공"),
    MEMBER_LOGOUT(HttpStatus.NO_CONTENT, "204", "로그아웃 성공"),

    /* MEMBER */
    MEMBER_DETAIL(HttpStatus.OK, "200", "회원정보 불러오기 성공"),
    MEMBER_UPDATE(HttpStatus.OK, "200", "회원정보 수정 성공"),
    MEMBER_DELETE(HttpStatus.NO_CONTENT, "204", "회원정보 삭제 성공"),
    MEMBER_EXISTS(HttpStatus.OK,"200","회원존재 여부 조회 성공"),
    MEMBER_ID_SEND(HttpStatus.OK,"200","아이디 메일 발송 완료"),
    MEMBER_PW_SEND(HttpStatus.OK,"200","임시 비밀번호 메일 발송 완료"),
    MEMBER_PROFILE_CREATE(HttpStatus.OK,"200","프로필 설정 완료"),
    MEMBER_PROFILE_UPDATE(HttpStatus.OK,"200","프로필 수정 완료"),
    MEMBER_PROFILE_READ(HttpStatus.OK,"200","프로필 조회 완료"),
    MEMBER_LEVEL_UP(HttpStatus.OK,"200","경험치 증가 완료"),
    MEMBER_GAME_RECORD_UPDATE(HttpStatus.OK, "200", "게임기록 업데이트 완료"),
    MEMBER_GAME_RECORD_READ(HttpStatus.OK, "200", "게임기록 조회 완료"),

    /* PLAYER */
    PLAYER_LEAVE(HttpStatus.OK,"200","게임 퇴장 완료"),
    PLAYER_JOIN(HttpStatus.OK, "200", "게임 참여 완료"),

    /* JWT */
    ACCESS_TOKEN_REISSUE(HttpStatus.OK, "200", "Access Token 재발급 성공"),
    TOKEN_INFO_CHECK(HttpStatus.OK, "200", "Token 정보 조회 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ResponseCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public ResponseEntity<ResResult> toResponse(Object data) {
        return new ResponseEntity<>(ResResult.builder()
            .responseCode(this)
            .code(this.code)
            .message(this.message)
            .data(data)
            .build(),HttpStatus.OK);
    }

}
