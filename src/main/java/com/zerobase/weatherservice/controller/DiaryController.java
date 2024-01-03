package com.zerobase.weatherservice.controller;

import com.zerobase.weatherservice.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping("/create/diary")
    void createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text
    ) {
        diaryService.createDiary(date, text);
    }
}
