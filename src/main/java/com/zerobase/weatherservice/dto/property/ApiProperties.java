package com.zerobase.weatherservice.dto.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openweathermap")
public record ApiProperties (
    String key
) {}