package com.example.demo.calendar.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

// calendar 패키지에서 발생한 예외를 담아 응답할 표준 에러 형태
@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp;  // 요청 시간
    private final int status;               // 에러 코드
    private final String error;             // 에러 코드명
    private final String message;           // 에러 메시지

    private ErrorResponse(HttpStatus status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status, message);
    }
}
