package com.hanss.ds.config;

import com.knuddels.jtokkit.api.EncodingType;
import lombok.Data;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public BatchingStrategy customTokenCountBatchingStrategy() {
        return new TokenCountBatchingStrategy(
                EncodingType.CL100K_BASE,  // Specify the encoding type
                100000,                      // Set the maximum input token count
                0.1                        // Set the reserve percentage
        );
    }
}