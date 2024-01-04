package com.zerobase.weatherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    private String status;
    private T data;
    private String message;

    public static <T> Response<T> success(T data) {
        return new Response<>(SUCCESS, data, null);
    }

    public static Response<Void> success() {
        return new Response<>(SUCCESS, null, null);
    }

    public static Response<String> error(String message) {
        return new Response<>(ERROR, null, message);
    }
}
