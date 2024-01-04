package com.zerobase.weatherservice.controller;

import com.zerobase.weatherservice.dto.DiaryDto;
import com.zerobase.weatherservice.dto.response.Response;
import com.zerobase.weatherservice.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping("/create/diary")
    public Response<Void> createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text
    ) {
        diaryService.createDiary(date, text);
        return Response.success();
    }

    @GetMapping("/read/diary")
    public Response<List<DiaryDto>> readDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return Response.success(diaryService.readDiary(date));
    }

    @GetMapping("/read/diaries")
    public Response<List<DiaryDto>> readDiaries (
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return Response.success(diaryService.readDiaries(startDate, endDate));
    }

    @PutMapping("/update/diary")
    public Response<Void> updateDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text
    ) {
        diaryService.updateDiary(date, text);
        return Response.success();
    }

    @DeleteMapping("/delete/diary")
    public Response<Void> deleteDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        diaryService.deleteDiary(date);
        return Response.success();
    }
}
