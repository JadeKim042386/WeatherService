package com.zerobase.weatherservice.service;

import com.zerobase.weatherservice.domain.Diary;
import com.zerobase.weatherservice.dto.DiaryDto;
import com.zerobase.weatherservice.exception.DiaryException;
import com.zerobase.weatherservice.repository.DateWeatherRepository;
import com.zerobase.weatherservice.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.weatherservice.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final WeatherService weatherService;

    /**
     * 매일 01시마다 날씨 정보를 DB에 저장
     * @throws DiaryException
     */
    @Scheduled(cron = "0 0 1 * * * ")
    public void saveWeatherDate() {
        weatherService.saveWeatherDate();
    }

    @Transactional
    public void createDiary(LocalDate date, String text) {
        Diary diary = Diary.setDateWeather(weatherService.getDateWeather(date));
        diary.setText(text);
        try {
            diaryRepository.save(diary);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            log.error("[{}] Diary 저장 실패 \n [TEXT]\n {}", date.toString(), text);
            throw new DiaryException(FAILED_SAVE_DIARY);
        }
    }

    public List<DiaryDto> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date).stream()
                .map(DiaryDto::fromEntity).collect(Collectors.toList());
    }

    public List<DiaryDto> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate).stream()
                .map(DiaryDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date)
                .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY, new EntityNotFoundException(date.toString())));
        nowDiary.setText(text);
    }

    @Transactional
    public void deleteDiary(LocalDate date) {
        try {
            diaryRepository.deleteAllByDate(date);
        } catch (IllegalArgumentException e) {
            throw new DiaryException(FAILED_DELETE_DIARY, e);
        }
    }
}
