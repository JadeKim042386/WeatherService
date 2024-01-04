package com.zerobase.weatherservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    NOT_FOUND_DIARY("다이어리가 존재하지 않습니다."),
    FAILED_GET_FROM_API("API로부터 데이터를 가져오는데 실패했습니다."),
    FAILED_PARSING_JSON("JSON 객체로 파싱하는데 실패했습니다."),
    FAILED_SAVE_WEATHER("날씨 정보를 DB에 저장하는데 실패했습니다."),
    FAILED_SAVE_DIARY("Diary를 DB에 저장하는데 실패했습니다."),
    FAILED_DELETE_DIARY("Diary를 DB에 삭제하는데 실패했습니다.")
    ;
    private final String description;
}
