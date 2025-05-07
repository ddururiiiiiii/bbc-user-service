package com.bookbookclub.bbc_user_service.global.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "report")
public class ReportProperties {
    private int blindThreshold;

    public void setBlindThreshold(int blindThreshold) {
        this.blindThreshold = blindThreshold;
    }
}
