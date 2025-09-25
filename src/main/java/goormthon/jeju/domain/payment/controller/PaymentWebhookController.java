package goormthon.jeju.domain.payment.controller;

import goormthon.jeju.domain.payment.controller.spec.PaymentWebhookControllerSpec;
import goormthon.jeju.domain.payment.gateway.TossPaymentGateway;
import goormthon.jeju.domain.payment.service.PaymentService;
import goormthon.jeju.global.common.ApiResponse;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/webhook/payments")
@RequiredArgsConstructor
public class PaymentWebhookController implements PaymentWebhookControllerSpec {

    private final PaymentService paymentService;
    private final TossPaymentGateway tossPaymentGateway;

    /**
     * 토스페이먼츠 웹후크 처리
     * eventType: PAYMENT_STATUS_CHANGED
     */
    @PostMapping("/toss")
    public ApiResponse<Void> tossWebhook(@RequestBody Map<String, Object> payload) {
        log.info("Toss webhook received: {}", payload);

        try {
            String eventType = (String) payload.get("eventType");

            if (!"PAYMENT_STATUS_CHANGED".equals(eventType)) {
                log.warn("Unsupported webhook event type: {}", eventType);
                return ApiResponse.success(null);
            }

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            if (data == null) {
                log.error("Webhook data is null");
                throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            String paymentKey = (String) data.get("paymentKey");
            String orderId = (String) data.get("orderId");
            String status = (String) data.get("status");
            String secret = (String) data.get("secret");

            // 토스페이먼츠에서 실제 결제 정보를 조회하여 검증
            Map<String, Object> paymentData = tossPaymentGateway.getPayment(paymentKey);

            // secret 검증
            String actualSecret = (String) paymentData.get("secret");
            if (!secret.equals(actualSecret)) {
                log.error("Invalid webhook secret. Expected: {}, Actual: {}", actualSecret, secret);
                throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            // 결제 상태별 처리
            switch (status) {
                case "DONE":
                    paymentService.completePaymentByOrderId(orderId, paymentKey);
                    log.info("Toss payment completed: orderId={}, paymentKey={}", orderId, paymentKey);
                    break;
                case "CANCELED":
                case "ABORTED":
                    paymentService.failPaymentByOrderId(orderId);
                    log.info("Toss payment canceled/aborted: orderId={}, status={}", orderId, status);
                    break;
                case "PARTIAL_CANCELED":
                    // 부분 취소 처리 - 현재는 로깅만
                    log.info("Toss payment partially canceled: orderId={}, paymentKey={}", orderId, paymentKey);
                    break;
                default:
                    log.info("Toss payment status updated: orderId={}, status={}", orderId, status);
                    break;
            }

            return ApiResponse.success(null);
        } catch (GlobalException e) {
            log.error("Toss webhook processing failed with known error", e);
            throw e;
        } catch (Exception e) {
            log.error("Toss webhook processing failed with unknown error", e);
            return ApiResponse.success(null); // 웹후크는 성공 응답을 반환해야 재시도를 방지
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