package goormthon.jeju.domain.verification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

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

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendSms(String to, String message) {
        if (to == null || to.trim().isEmpty()) {
            log.warn("SMS 전송 건너뜀: 전화번호가 없습니다");
            return;
        }

        try {
            Message msg = new Message();
            msg.setFrom(fromNumber);
            msg.setTo(to);
            msg.setText(message);

            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(msg));

            log.info("SMS sent successfully to {}: messageId={}", to, response.getMessageId());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage());
            throw new RuntimeException("SMS 전송 실패", e);
        }
    }
}