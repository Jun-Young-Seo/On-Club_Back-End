package com.springboot.club_house_api_server.apn.config;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class APNsConfig {

    private final String teamId =  "6VF8H2JP9M";
    private final String keyId = "ZXXYLTD332";

    @Bean
    public ApnsClient apnsClient() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return new ApnsClientBuilder()
                .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                .setSigningKey(
                        ApnsSigningKey.loadFromInputStream(new FileInputStream("AuthKey_ZXXYLTD332.p8"), teamId, keyId)
                )
                .build();
    }
}
