package com.hanss.ds.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Data
public class AppConfig {
    @Value("${webdav.server.url}")
    private String webdavServerUrl;

    @Value("${webdav.username}")
    private String webdavUsername;

    @Value("${webdav.password}")
    private String webdavPassword;
}