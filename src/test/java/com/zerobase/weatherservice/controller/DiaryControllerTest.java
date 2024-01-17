package com.zerobase.weatherservice.controller;

import com.zerobase.weatherservice.domain.Diary;
import com.zerobase.weatherservice.dto.DiaryDto;
import com.zerobase.weatherservice.service.DiaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Diary 컨트롤러")
@WebMvcTest(DiaryController.class)
class DiaryControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private DiaryService diaryService;

    @DisplayName("다이어리 추가")
    @Test
    void createDiary() throws Exception {
        //given
        willDoNothing().given(diaryService).createDiary(any(), anyString());
        //when
        mvc.perform(
                post("/create/diary")
                        .queryParam("date", LocalDate.now().toString())
                        .content("text")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
        //then
    }

    @DisplayName("특정 날짜의 다이어리 조회")
    @Test
    void readDiary() throws Exception {
        //given
        given(diaryService.readDiary(any()))
                .willReturn(List.of(generateDiaryDto()));
        //when
        mvc.perform(
                get("/read/diary")
                        .queryParam("date", LocalDate.now().toString())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.size()").value(1));
        //then
    }

    @DisplayName("특정 기간 내의 다이어리 조회")
    @Test
    void readDiaries() throws Exception {
        //given
        given(diaryService.readDiaries(any(), any()))
                .willReturn(List.of(generateDiaryDto()));
        //when
        mvc.perform(
                        get("/read/diaries")
                                .queryParam("startDate", LocalDate.now().minusDays(3).toString())
                                .queryParam("endDate", LocalDate.now().toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.size()").value(1));
        //then
    }

    @DisplayName("다이어리 수정")
    @Test
    void updateDiary() throws Exception {
        //given
        willDoNothing().given(diaryService).updateDiary(any(), anyString());
        //when
        mvc.perform(
                put("/update/diary")
                        .queryParam("date", LocalDate.now().toString())
                        .content("updatedText")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
        //then
    }

    @DisplayName("다이어리 삭제")
    @Test
    void deleteDiary() throws Exception {
        //given
        willDoNothing().given(diaryService).deleteDiary(any());
        //when
        mvc.perform(
                        delete("/delete/diary")
                                .queryParam("date", LocalDate.now().toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
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

    private static DiaryDto generateDiaryDto() {
        return DiaryDto.fromEntity(generateDiary());
    }
}