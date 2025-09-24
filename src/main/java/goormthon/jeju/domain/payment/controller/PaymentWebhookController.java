package goormthon.jeju.domain.payment.controller;

import goormthon.jeju.domain.payment.service.PaymentService;
import goormthon.jeju.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/webhook/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentService paymentService;

    @PostMapping("/toss")
    public ApiResponse<Void> tossWebhook(@RequestBody Map<String, Object> payload) {
        log.info("Toss webhook received: {}", payload);

        try {
            String paymentKey = (String) payload.get("paymentKey");
            String orderId = (String) payload.get("orderId");
            String status = (String) payload.get("status");

            if ("DONE".equals(status)) {
                paymentService.completePaymentByOrderId(orderId, paymentKey);
                log.info("Toss payment completed: orderId={}, paymentKey={}", orderId, paymentKey);
            } else if ("CANCELED".equals(status)) {
                paymentService.failPaymentByOrderId(orderId);
                log.info("Toss payment canceled: orderId={}", orderId);
            }

            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("Toss webhook processing failed", e);
            return ApiResponse.success(null);
        }
    }

    @PostMapping("/kakao")
    public ApiResponse<Void> kakaoWebhook(@RequestBody Map<String, Object> payload) {
        log.info("Kakao webhook received: {}", payload);

        try {
            String tid = (String) payload.get("tid");
            String partnerOrderId = (String) payload.get("partner_order_id");
            String status = (String) payload.get("status");

            if ("SUCCESS".equals(status)) {
                paymentService.completePaymentByOrderId(partnerOrderId, tid);
                log.info("Kakao payment completed: orderId={}, tid={}", partnerOrderId, tid);
            } else if ("CANCEL".equals(status) || "FAIL".equals(status)) {
                paymentService.failPaymentByOrderId(partnerOrderId);
                log.info("Kakao payment failed: orderId={}, status={}", partnerOrderId, status);
            }

            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("Kakao webhook processing failed", e);
            return ApiResponse.success(null);
        }
    }
}