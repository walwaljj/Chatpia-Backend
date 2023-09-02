package com.springles.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.springles.exception.constants.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatus;


/**
 * Global Exception Handler에서 발생한 에러에 대한 응답 처리를 관리
 */
@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private ErrorCode errorCode;
    private String code;
    private String message;
    private HttpStatus httpStatus;

    public ErrorResponse(ErrorCode errorCode) {
//        this.code = errorCode.getStatus().toString();
//        this.errorCode = errorCode;
        this.httpStatus = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }

    @Builder
    public ErrorResponse(final ErrorCode code, final String message) {
        this.httpStatus = code.getStatus();
        this.message = message;
    }

    public static ErrorResponse of(final ErrorCode code, final String reason) {
        return new ErrorResponse(code, reason);
    }
}