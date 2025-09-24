package goormthon.jeju.domain.verification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${sms.api-key}")
    private String apiKey;

    @Value("${sms.api-secret}")
    private String apiSecret;

    @Value("${sms.from-number}")
    private String fromNumber;

    public void sendSms(String to, String message) {
        log.info("Sending SMS to {}: {}", to, message);
        log.info("SMS API Key: {}", apiKey);
        log.info("From Number: {}", fromNumber);
    }
}