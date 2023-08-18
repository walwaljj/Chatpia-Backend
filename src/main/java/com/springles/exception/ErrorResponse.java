package com.springles.exception;

import com.springles.exception.constants.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


/**
 * Global Exception Handler에서 발생한 에러에 대한 응답 처리를 관리
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private HttpStatus status;           // 에러 상태 코드
    private String resultMsg;           // 에러 메시지
    private String reason;              // 에러 이유

    /**
     * ErrorResponse 생성자
     *
     * @param code   ErrorCode
     * @param reason String
     */
    @Builder
    protected ErrorResponse(final ErrorCode code, final String reason) {
        this.resultMsg = code.getMessage();
        this.status = code.getStatus();
        this.reason = reason;
    }

    /**
     * Global Exception 전송 타입
     *
     * @param code   ErrorCode
     * @param reason String
     * @return ErrorResponse
     */
    public static ErrorResponse of(final ErrorCode code, final String reason) {
        return new ErrorResponse(code, reason);
    }

}