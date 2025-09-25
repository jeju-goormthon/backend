package goormthon.jeju.domain.payment.gateway;

import goormthon.jeju.domain.payment.dto.gateway.*;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentGateway implements PaymentGateway {

    private final RestTemplate restTemplate;

    @Value("${payment.toss.secret-key}")
    private String secretKey;

    @Value("${payment.toss.api-url:https://api.tosspayments.com}")
    private String apiUrl;

    /**
     * 토스페이먼츠에서는 prepare 단계가 프론트엔드에서 처리됩니다.
     * 이 메서드는 주문 정보만 생성하고 실제 결제창은 프론트엔드 SDK에서 처리합니다.
     */
    @Override
    public PaymentPrepareResponse prepare(PaymentPrepareRequest request) {
        // 토스페이먼츠는 서버에서 결제창을 생성하지 않습니다.
        // 주문 ID와 금액 정보만 반환하여 프론트엔드에서 사용하도록 합니다.
        return PaymentPrepareResponse.builder()
                .paymentKey(null) // 결제 승인 후에 생성됩니다
                .orderId(request.getOrderId())
                .checkoutUrl(null) // 프론트엔드 SDK에서 처리합니다
                .amount(request.getAmount())
                .build();
    }

    /**
     * 결제 승인 (Payment Confirmation)
     * 프론트엔드에서 결제 완료 후 서버에서 최종 승인을 처리합니다.
     */
    public PaymentApprovalResponse confirm(PaymentApprovalRequest request) {
        try {
            String url = apiUrl + "/v1/payments/confirm";

            Map<String, Object> body = new HashMap<>();
            body.put("paymentKey", request.getPaymentKey());
            body.put("orderId", request.getOrderId());
            body.put("amount", request.getAmount());

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return PaymentApprovalResponse.builder()
                        .paymentKey((String) responseBody.get("paymentKey"))
                        .orderId((String) responseBody.get("orderId"))
                        .transactionId((String) responseBody.get("transactionKey"))
                        .amount(((Number) responseBody.get("totalAmount")).longValue())
                        .status((String) responseBody.get("status"))
                        .approvedAt(LocalDateTime.now())
                        .build();
            }

            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        } catch (Exception e) {
            log.error("Toss payment confirmation failed", e);
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }
    }

    /**
     * 결제 정보 조회
     */
    public Map<String, Object> getPayment(String paymentKey) {
        try {
            String url = apiUrl + "/v1/payments/" + paymentKey;

            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            throw new GlobalException(ErrorCode.PAYMENT_NOT_FOUND);
        } catch (Exception e) {
            log.error("Toss payment inquiry failed for paymentKey: {}", paymentKey, e);
            throw new GlobalException(ErrorCode.PAYMENT_NOT_FOUND);
        }
    }

    /**
     * PaymentGateway 인터페이스 호환성을 위한 approve 메서드
     * 실제로는 confirm 메서드를 사용합니다.
     */
    @Override
    public PaymentApprovalResponse approve(PaymentApprovalRequest request) {
        return confirm(request);
    }

    @Override
    public PaymentCancelResponse cancel(PaymentCancelRequest request) {
        try {
            String url = apiUrl + "/v1/payments/" + request.getPaymentKey() + "/cancel";

            Map<String, Object> body = new HashMap<>();
            body.put("cancelReason", request.getCancelReason());
            body.put("cancelAmount", request.getCancelAmount());

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return PaymentCancelResponse.builder()
                        .paymentKey((String) responseBody.get("paymentKey"))
                        .orderId((String) responseBody.get("orderId"))
                        .transactionId((String) responseBody.get("transactionKey"))
                        .cancelAmount(((Number) responseBody.get("cancelAmount")).longValue())
                        .cancelReason((String) responseBody.get("cancelReason"))
                        .canceledAt(LocalDateTime.now())
                        .build();
            }

            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        } catch (Exception e) {
            log.error("Toss payment cancel failed", e);
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}