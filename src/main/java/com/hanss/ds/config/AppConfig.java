package com.hanss.ds.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AppConfig {
    @Value("${webdav.server.url}")
    private String webdavServerUrl;

    @Value("${webdav.username}")
    private String webdavUsername;

    @Value("${webdav.password}")
    private String webdavPassword;

}