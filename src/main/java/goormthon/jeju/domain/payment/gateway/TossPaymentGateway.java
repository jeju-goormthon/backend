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

    @Override
    public PaymentPrepareResponse prepare(PaymentPrepareRequest request) {
        try {
            String url = apiUrl + "/v1/payments";

            Map<String, Object> body = new HashMap<>();
            body.put("orderId", request.getOrderId());
            body.put("orderName", request.getOrderName());
            body.put("amount", request.getAmount());
            body.put("customerName", request.getCustomerName());
            body.put("successUrl", request.getSuccessUrl());
            body.put("failUrl", request.getFailUrl());

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return PaymentPrepareResponse.builder()
                        .paymentKey((String) responseBody.get("paymentKey"))
                        .orderId((String) responseBody.get("orderId"))
                        .checkoutUrl((String) responseBody.get("checkoutUrl"))
                        .amount(((Number) responseBody.get("totalAmount")).longValue())
                        .build();
            }

            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        } catch (Exception e) {
            log.error("Toss payment prepare failed", e);
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }
    }

    @Override
    public PaymentApprovalResponse approve(PaymentApprovalRequest request) {
        try {
            String url = apiUrl + "/v1/payments/" + request.getPaymentKey() + "/approve";

            Map<String, Object> body = new HashMap<>();
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
            log.error("Toss payment approval failed", e);
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }
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