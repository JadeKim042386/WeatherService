package com.zerobase.weatherservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DiaryException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

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
