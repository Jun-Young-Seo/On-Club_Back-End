package com.springboot.club_house_api_server.apn.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.springboot.club_house_api_server.apn.config.APNsConfig;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class APNsService {

    private final ApnsClient apnsClient;

    @Value("${apn.bundle.id}")
    private String bundleId;

    public void sendTestPush(String deviceToken) throws Exception {
        String payload = new SimpleApnsPayloadBuilder()
                .setAlertTitle("On-Club 알림")
                .setAlertBody("테스트 푸시입니다! 연결 성공")
                .setSound("default")
                .build();

        SimpleApnsPushNotification notification =
                new SimpleApnsPushNotification(deviceToken, bundleId, payload);

        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> future =
                apnsClient.sendNotification(notification);

        PushNotificationResponse<SimpleApnsPushNotification> response = future.get();

        if (response.isAccepted()) {
            System.out.println("푸시 전송 성공!");
        } else {
            System.err.println("푸시 거절됨: " + response.getRejectionReason());
            response.getTokenInvalidationTimestamp().ifPresent(ts ->
                    System.err.println("이 토큰은 " + ts + " 이후로 무효입니다."));
        }
    }
}
