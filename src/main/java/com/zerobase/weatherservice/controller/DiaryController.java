package com.zerobase.weatherservice.controller;

import com.zerobase.weatherservice.dto.DiaryDto;
import com.zerobase.weatherservice.dto.response.Response;
import com.zerobase.weatherservice.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @Operation(summary = "다이어리 추가", responses = {
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @PostMapping(value = "/create/diary", produces = "application/json")
    public Response<Void> createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text
    ) {
        diaryService.createDiary(date, text);
        return Response.success();
    }

    @Operation(summary = "특정 날짜의 다이어리 조회", responses = {
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @GetMapping(value = "/read/diary", produces = "application/json")
    public Response<List<DiaryDto>> readDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(name = "날짜 형식: yyyy-MM-dd", example = "2024-01-05") LocalDate date
    ) {
        return Response.success(diaryService.readDiary(date));
    }

    @Operation(summary = "특정 기간 내의 다이어리 조회", description = "startDate ~ endDate 기간 내의 다이어리를 조회합니다.", responses = {
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @GetMapping(value = "/read/diaries", produces = "application/json")
    public Response<List<DiaryDto>> readDiaries (
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(name = "첫 날 [날짜 형식: yyyy-MM-dd]", example = "2024-01-05") LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(name = "마지막 날 [날짜 형식: yyyy-MM-dd]", example = "2024-01-05") LocalDate endDate
    ) {
        return Response.success(diaryService.readDiaries(startDate, endDate));
    }

    @Operation(summary = "특정 날짜의 다이어리 수정", responses = {
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @PutMapping(value = "/update/diary", produces = "application/json")
    public Response<Void> updateDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(name = "날짜 형식: yyyy-MM-dd", example = "2024-01-05") LocalDate date,
            @RequestBody String text
    ) {
        diaryService.updateDiary(date, text);
        return Response.success();
    }

    @Operation(summary = "특정 날짜의 다이어리 삭제", responses = {
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @DeleteMapping(value = "/delete/diary", produces = "application/json")
    public Response<Void> deleteDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(name = "날짜 형식: yyyy-MM-dd", example = "2024-01-05") LocalDate date
    ) {
        diaryService.deleteDiary(date);
        return Response.success();
    }
}
