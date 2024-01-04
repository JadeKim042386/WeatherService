package com.zerobase.weatherservice.exception;

import com.zerobase.weatherservice.WeatherServiceApplication;
import com.zerobase.weatherservice.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger logger = LoggerFactory.getLogger(WeatherServiceApplication.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> handleMethodArgsException(MethodArgumentNotValidException e) {
        logger.error("MethodArgumentNotValidException is occurred.", e);
        return Response.error(INVALID_REQUEST.getDescription());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Response<String> handleDataViolationException(DataIntegrityViolationException e) {
        logger.error("DataIntegrityViolationException is occurred.", e);
        return Response.error(INVALID_REQUEST.getDescription());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Response<String> handleAllException(Exception e) {
        logger.error("Exception is occurred.", e);
        return Response.error(INTERNAL_SERVER_ERROR.getDescription());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DiaryException.class)
    public Response<String> handleDiaryException(DiaryException e) {
        return Response.error(e.getErrorMessage());
    }
}
