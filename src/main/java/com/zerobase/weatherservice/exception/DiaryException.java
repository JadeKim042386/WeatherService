package com.zerobase.weatherservice.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;

    public DiaryException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public DiaryException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription() + "|" + causeException.getMessage();
        initCause(causeException);
    }
}
