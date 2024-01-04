package com.zerobase.weatherservice.service;

import com.zerobase.weatherservice.WeatherServiceApplication;
import com.zerobase.weatherservice.domain.DateWeather;
import com.zerobase.weatherservice.domain.Diary;
import com.zerobase.weatherservice.dto.property.ApiProperties;
import com.zerobase.weatherservice.exception.DiaryException;
import com.zerobase.weatherservice.repository.DateWeatherRepository;
import com.zerobase.weatherservice.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zerobase.weatherservice.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final static Logger logger = LoggerFactory.getLogger(WeatherServiceApplication.class);
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    private final ApiProperties apiProperties;

    /**
     * 매일 01시마다 날씨 정보를 DB에 저장
     * @throws DiaryException
     */
    @Transactional
    @Scheduled(cron = "0 0 1 * * * ")
    public void saveWeatherDate() {
        try {
            dateWeatherRepository.save(getWeatherFromApi());
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("[{}] 날씨 저장 실패", LocalDate.now().toString());
            throw new DiaryException(FAILED_SAVE_WEATHER);
        }
    }

    @Transactional
    public void createDiary(LocalDate date, String text) {
        Diary diary = Diary.setDateWeather(getDateWeather(date));
        diary.setText(text);
        try {
            diaryRepository.save(diary);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("[{}] Diary 저장 실패 \n [TEXT]\n {}", date.toString(), text);
            throw new DiaryException(FAILED_SAVE_DIARY);
        }
    }

    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
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

    /**
     * openweathermap API로부터 날씨 정보를 받음
     * @throws DiaryException
     */
    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiProperties.key();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            logger.error("[{}]에 요청했지만 실패했습니다.", apiUrl);
            throw new DiaryException(FAILED_GET_FROM_API, e);
        }
    }

    /**
     * String type의 API Response를 JSON 객체로 파싱하여 매핑
     * @param jsonString openweathermap API로부터 받은 Response
     * @throws DiaryException
     */
    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            logger.error("다음과 같은 Response를 JSON 객체로 파싱하는데 실패했습니다.\n {}", jsonString);
            throw new DiaryException(FAILED_PARSING_JSON, e);
        }
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }

    private DateWeather getWeatherFromApi() {
        String weatherData = getWeatherString();
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        return DateWeather.builder()
                    .date(LocalDate.now())
                    .weather(parsedWeather.get("main").toString())
                    .icon(parsedWeather.get("icon").toString())
                    .temperature((Double) parsedWeather.get("temp"))
                    .build();
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        // 현재 날짜의 날씨 정보가 없다면 API에 요청
        if (dateWeatherListFromDB.isEmpty()){
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }
}
