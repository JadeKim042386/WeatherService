package com.zerobase.weatherservice.dto;

import com.zerobase.weatherservice.domain.Diary;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DiaryDto (
        int id,
        String weather,
        String icon,
        double temperature,
        String text,
        LocalDate date
) {
    public static DiaryDto fromEntity(Diary diary) {
        return DiaryDto.builder()
                .id(diary.getId())
                .weather(diary.getWeather())
                .icon(diary.getIcon())
                .temperature(diary.getTemperature())
                .date(diary.getDate())
                .build();
    }
}
