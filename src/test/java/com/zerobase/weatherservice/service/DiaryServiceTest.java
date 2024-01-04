package com.zerobase.weatherservice.service;

import com.zerobase.weatherservice.domain.DateWeather;
import com.zerobase.weatherservice.domain.Diary;
import com.zerobase.weatherservice.dto.property.ApiProperties;
import com.zerobase.weatherservice.exception.DiaryException;
import com.zerobase.weatherservice.exception.ErrorCode;
import com.zerobase.weatherservice.repository.DateWeatherRepository;
import com.zerobase.weatherservice.repository.DiaryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.zerobase.weatherservice.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@EnableConfigurationProperties(ApiProperties.class)
class DiaryServiceTest {
    @InjectMocks private DiaryService diaryService;
    @Mock private DiaryRepository diaryRepository;
    @Mock private DateWeatherRepository dateWeatherRepository;

    @DisplayName("다이어리 추가")
    @Test
    void createDiary() {
        //given
        given(dateWeatherRepository.findAllByDate(any()))
                .willReturn(List.of(
                        DateWeather.builder()
                                .date(LocalDate.now())
                                .weather("cloud")
                                .icon("icon")
                                .temperature(11.1)
                                .build()
                ));
        given(diaryRepository.save(any()))
                .willReturn(generateDiary());
        //when
        diaryService.createDiary(LocalDate.now(), "text");
        ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);
        //then
        verify(diaryRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getText()).isEqualTo("text");
        assertThat(captor.getValue().getDate()).isEqualTo(LocalDate.now());
        assertThat(captor.getValue().getWeather()).isEqualTo("cloud");
        assertThat(captor.getValue().getIcon()).isEqualTo("icon");
        assertThat(captor.getValue().getTemperature()).isEqualTo(11.1);
    }

    @DisplayName("[예외 - 저장 실패] 다이어리 추가")
    @Test
    void createDiary_failedSave() {
        //given
        given(dateWeatherRepository.findAllByDate(any()))
                .willReturn(List.of(
                        DateWeather.builder()
                                .date(LocalDate.now())
                                .weather("cloud")
                                .icon("icon")
                                .temperature(11.1)
                                .build()
                ));
        given(diaryRepository.save(any()))
                .willThrow(new DiaryException(FAILED_SAVE_DIARY));
        //when
        assertThatThrownBy(() -> diaryService.createDiary(LocalDate.now(), "text"))
                .isInstanceOf(DiaryException.class)
                .hasFieldOrPropertyWithValue("errorCode", FAILED_SAVE_DIARY);
        //then
    }

    @DisplayName("특정 날짜의 다이어리 조회")
    @Test
    void readDiary() {
        //given
        given(diaryRepository.findAllByDate(any()))
                .willReturn(List.of(generateDiary()));
        //when
        diaryService.readDiary(LocalDate.now());
        //then
    }

    @DisplayName("주어진 날짜 기간의 다이어리 조회")
    @Test
    void readDiaries() {
        //given
        given(diaryRepository.findAllByDateBetween(any(), any()))
                .willReturn(List.of(generateDiary()));
        //when
        diaryService.readDiaries(LocalDate.now().minusDays(3), LocalDate.now());
        //then
    }

    @DisplayName("특정 날짜의 다이어리 수정")
    @Test
    void updateDiary() {
        //given
        Diary savedDiary = generateDiary();
        given(diaryRepository.getFirstByDate(any()))
                .willReturn(Optional.of(savedDiary));
        //when
        diaryService.updateDiary(LocalDate.now(), "updatedText");
        //then
        assertThat(savedDiary.getText()).isEqualTo("updatedText");
    }

    @DisplayName("[예외 - NOT FOUND DIARY]특정 날짜의 다이어리 수정")
    @Test
    void updateDiary_NotFoundDiary() {
        //given
        given(diaryRepository.getFirstByDate(any()))
                .willThrow(new DiaryException(NOT_FOUND_DIARY));
        //when
        assertThatThrownBy(() -> diaryService.updateDiary(LocalDate.now(), "updatedText"))
                .isInstanceOf(DiaryException.class)
                .hasFieldOrPropertyWithValue("errorCode", NOT_FOUND_DIARY);
        //then
    }

    @DisplayName("특정 날짜의 다이어리 삭제")
    @Test
    void deleteDiary() {
        //given
        willDoNothing().given(diaryRepository).deleteAllByDate(any());
        //when
        diaryService.deleteDiary(LocalDate.now());
        //then
    }

    @DisplayName("[예외 - 삭제 실패] 특정 날짜의 다이어리 삭제")
    @Test
    void deleteDiary_failedDelete() {
        //given
        willThrow(new DiaryException(FAILED_DELETE_DIARY))
                .given(diaryRepository).deleteAllByDate(any());
        //when
        assertThatThrownBy(() -> diaryService.deleteDiary(LocalDate.now()))
                .isInstanceOf(DiaryException.class)
                .hasFieldOrPropertyWithValue("errorCode", FAILED_DELETE_DIARY);
        //then
    }

    private static Diary generateDiary() {
        return Diary.builder()
                .id(1)
                .text("text")
                .date(LocalDate.now())
                .weather("cloud")
                .icon("icon")
                .temperature(11.1)
                .build();
    }
}