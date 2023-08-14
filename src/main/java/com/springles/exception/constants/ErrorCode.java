package com.springles.exception.constants;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCode {

    /* COMMON */
    NOT_AUTHORIZED_CONTENT(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다."),
    REQUEST_EMPTY(HttpStatus.BAD_REQUEST, "요청이 올바르지 않습니다."),

    /* JWT */
    NO_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "로그인 정보가 존재하지 않습니다. 다시 로그인해 주세요."),
    NOT_AUTHORIZED_TOKEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "로그인 정보가 유효하지 않습니다."),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "로그인 정보 형식이 올바르지 않습니다."),
    INVALID_TOKEN_STRUCTURE(HttpStatus.UNAUTHORIZED, "로그인 정보가 올바르지 않습니다."),
    MODIFIED_TOKEN_DETECTED(HttpStatus.UNAUTHORIZED, "로그인 정보가 변경되었습니다."),

    /* CHATROOM */
    PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "비밀번호를 입력하세요."),
    CAPACITY_WRONG(HttpStatus.BAD_REQUEST, "정원은 최소 5명 이상 10명 이하 입니다."),
    TITLE_EMPTY(HttpStatus.BAD_REQUEST, "방 제목을 입력해주세요."),
    OPEN_PASSWORD(HttpStatus.BAD_REQUEST, "공개방은 비밀번호를 입력할 수 없습니다."),

    /* MEMBER */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 회원입니다."),
    WRONG_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
