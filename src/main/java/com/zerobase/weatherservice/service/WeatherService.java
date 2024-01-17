package com.zerobase.weatherservice.service;

import com.zerobase.weatherservice.domain.DateWeather;
import com.zerobase.weatherservice.dto.property.ApiProperties;
import com.zerobase.weatherservice.exception.DiaryException;
import com.zerobase.weatherservice.repository.DateWeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.zerobase.weatherservice.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {
    private static final String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=";
    private final DateWeatherRepository dateWeatherRepository;
    private final ApiProperties apiProperties;

    public void saveWeatherDate() {
        try {
            dateWeatherRepository.save(getWeatherFromApi());
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            log.error("[{}] 날씨 저장 실패", LocalDate.now().toString());
            throw new DiaryException(FAILED_SAVE_WEATHER);
        }
    }

    public DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        // 현재 날짜의 날씨 정보가 없다면 API에 요청
        if (dateWeatherListFromDB.isEmpty()){
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
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

    /**
     * openweathermap API로부터 날씨 정보를 받음
     * @throws DiaryException
     */
    private String getWeatherString() {
        try {
            URL url = new URL(apiUrl + apiProperties.key());
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
            log.error("[{}]에 요청했지만 실패했습니다.", apiUrl);
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
            log.error("다음과 같은 Response를 JSON 객체로 파싱하는데 실패했습니다.\n {}", jsonString);
            throw new DiaryException(FAILED_PARSING_JSON, e);
        }
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        if (weatherArray.isEmpty()) {
            log.error("weather 정보가 없습니다.");
            throw new DiaryException(NOT_EXIST_DATA);
        }
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }
}
