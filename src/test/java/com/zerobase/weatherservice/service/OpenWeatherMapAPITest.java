package com.zerobase.weatherservice.service;

import com.zerobase.weatherservice.domain.DateWeather;
import com.zerobase.weatherservice.exception.DiaryException;
import com.zerobase.weatherservice.exception.ErrorCode;
import com.zerobase.weatherservice.repository.DateWeatherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class OpenWeatherMapAPITest {
    @Autowired private DiaryService diaryService;
    @MockBean
    private DateWeatherRepository dateWeatherRepository;

    @DisplayName("날씨 정보를 DB에 저장")
    @Test
    void getWeatherString() {
        //given
        given(dateWeatherRepository.save(any()))
                .willReturn(DateWeather.builder()
                        .date(LocalDate.now())
                        .weather("cloud")
                        .icon("icon")
                        .temperature(11.1)
                        .build());
        //when
        diaryService.saveWeatherDate();
        ArgumentCaptor<DateWeather> captor = ArgumentCaptor.forClass(DateWeather.class);
        //then
        verify(dateWeatherRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getDate()).isEqualTo(LocalDate.now());
        assertThat(captor.getValue().getWeather()).isEqualTo("cloud");
        assertThat(captor.getValue().getIcon()).isEqualTo("icon");
        assertThat(captor.getValue().getTemperature()).isEqualTo(11.1);
    }

    @DisplayName("[예외] 날씨 정보를 DB에 저장")
    @Test
    void getWeatherString_failedSave() {
        //given
        given(dateWeatherRepository.save(any()))
                .willThrow(new DiaryException(ErrorCode.FAILED_SAVE_WEATHER));
        //when
        assertThatThrownBy(() -> diaryService.saveWeatherDate())
                .isInstanceOf(DiaryException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAILED_SAVE_WEATHER);
        //then
    }
}
