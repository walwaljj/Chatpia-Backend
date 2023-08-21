package com.springles.exception.constants;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCode {

    /* COMMON */
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST,"잘못된 요청입니다."),
    REQUEST_BODY_MISSING_ERROR(HttpStatus.BAD_REQUEST, "@RequestBody 데이터가 존재하지 않습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST," 유효하지 않은 타입입니다."),
    MISSING_REQUEST_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, "Request Parameter가 존재하지 않습니다."),
    IO_ERROR(HttpStatus.BAD_REQUEST,"입력/출력 값이 유효하지 않습니다."),
    REQUEST_EMPTY(HttpStatus.BAD_REQUEST, "요청이 올바르지 않습니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST,"Json 파싱을 실패했습니다."),
    JACKSON_PROCESS_ERROR(HttpStatus.BAD_REQUEST, "com.fasterxml.jackson.core Exception"),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "요청한 리소스가 존재하지 않습니다."),
    NULL_POINT_ERROR(HttpStatus.NOT_FOUND, "Null Point Exception"),
    NOT_VALID_ERROR(HttpStatus.NOT_FOUND, " @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않습니다."),
    NOT_VALID_HEADER_ERROR(HttpStatus.NOT_FOUND, "Header에 데이터가 존재하지 않습니다. "),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 오류가 발생했습니다."),
    NOT_AUTHORIZED_CONTENT(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다."),

    /* JWT */
    NO_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "로그인 정보가 존재하지 않습니다. 다시 로그인해 주세요."),
    NOT_AUTHORIZED_TOKEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "로그인 정보가 유효하지 않습니다."),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "로그인 정보 형식이 올바르지 않습니다."),
    INVALID_TOKEN_STRUCTURE(HttpStatus.UNAUTHORIZED, "로그인 정보가 올바르지 않습니다."),
    MODIFIED_TOKEN_DETECTED(HttpStatus.UNAUTHORIZED, "로그인 정보가 변경되었습니다."),

    /* CHATROOM */
    OPEN_ROOM_ERROR(HttpStatus.BAD_REQUEST, "공개방은 비밀번호를 입력할 수 없습니다."),
    CLOSE_ROOM_ERROR(HttpStatus.BAD_REQUEST, "비밀방은 비밀번호를 입력해야 합니다."),
    PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "비밀번호를 입력하세요."),
    CAPACITY_WRONG(HttpStatus.BAD_REQUEST, "정원은 최소 5명 이상 10명 이하 입니다."),
    TITLE_EMPTY(HttpStatus.BAD_REQUEST, "방 제목을 입력해주세요."),
    OPEN_PASSWORD(HttpStatus.BAD_REQUEST, "공개방은 비밀번호를 입력할 수 없습니다."),
    NOT_FOUND_ROOM(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다."),
    USER_NOT_OWNER(HttpStatus.UNAUTHORIZED, "수정 권한이 없습니다."),

    /* MEMBER */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 회원입니다."),
    WRONG_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

//    NULL_MEMBERNAME(HttpStatus.BAD_REQUEST, "아이디를 입력해주세요."),
//    OUT_OF_CHARACTER_LIMIT_MEMBERNAME(HttpStatus.BAD_REQUEST, "아이디는 6 ~ 20자 사이여야 합니다."),
//    INVALID_MEMBERNAME(HttpStatus.BAD_REQUEST, "아이디는 영문 대소문자(a-z, A-Z), 숫자(0-9)만 입력 할 수 있습니다."),
    EXIST_MEMBERNAME(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
//    NULL_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요."),
//    OUT_OF_CHARACTER_LIMIT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 6자 이상이여야 합니다."),
//    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 영문 대소문자(a-z, A-Z), 숫자(0-9), 특수문자(!,@,#,$,%,^,&,*)만 입력할 수 있습니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호 확인 값이 일치하지 않습니다."),
//    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "아이디와 일치하는 회원정보가 없습니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호를 잘못 입력했습니다."),
    DELETED_MEMBER(HttpStatus.BAD_REQUEST, "탈퇴한 회원입니다."),
    FAIL_SEND_MEMBER_ID(HttpStatus.INTERNAL_SERVER_ERROR,"아이디 메일 전송을 실패하였습니다."),
    FAIL_SEND_MEMBER_PW(HttpStatus.INTERNAL_SERVER_ERROR,"비밀번호 메일 전송을 실패하였습니다."),
    NOT_FOUND_INPUT_VALUE_MEMBER(HttpStatus.NOT_FOUND, "입력한 정보와 일치하는 회원정보가 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
