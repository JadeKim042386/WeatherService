package com.zerobase.weatherservice.exception;

import com.zerobase.weatherservice.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.zerobase.weatherservice.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.zerobase.weatherservice.exception.ErrorCode.INVALID_REQUEST;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgsException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException is occurred.", e);
        return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException is occurred.", e);
        return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAllException(Exception e) {
        log.error("Exception is occurred.", e);
        return new ErrorResponse(
                INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR.getDescription()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DiaryException.class)
    public ErrorResponse handleDiaryException(DiaryException e) {
        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }
}
