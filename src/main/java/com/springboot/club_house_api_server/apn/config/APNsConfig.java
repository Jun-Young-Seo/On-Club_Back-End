package com.springboot.club_house_api_server.apn.config;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class APNsConfig {

    @Value("${apn.bundle.id}")
    private String bundleId;
    @Value("${apn.team.id}")
    private String teamId;
    @Value("${apn.key.id}")
    private String keyId ;
    @Value("${apn.key}")
    private String key ;

    @Bean
    public ApnsClient apnsClient() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String normalizedPem = key.replace("\\n", "\n");
//        System.out.println(normalizedPem);
        try (ByteArrayInputStream keyStream = new ByteArrayInputStream(normalizedPem.getBytes())) {
            return new ApnsClientBuilder()
                    .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setSigningKey(ApnsSigningKey.loadFromInputStream(keyStream, teamId, keyId))
                    .build();
        }
    }
}
