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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoPaymentGateway implements PaymentGateway {

    private final RestTemplate restTemplate;

    @Value("${payment.kakao.admin-key}")
    private String adminKey;

    @Value("${payment.kakao.api-url:https://kapi.kakao.com}")
    private String apiUrl;

    @Override
    public PaymentPrepareResponse prepare(PaymentPrepareRequest request) {
        try {
            String url = apiUrl + "/v1/payment/ready";

            Map<String, Object> body = new HashMap<>();
            body.put("cid", "TC0ONETIME");
            body.put("partner_order_id", request.getOrderId());
            body.put("partner_user_id", request.getCustomerPhoneNumber());
            body.put("item_name", request.getOrderName());
            body.put("quantity", 1);
            body.put("total_amount", request.getAmount());
            body.put("tax_free_amount", 0);
            body.put("approval_url", request.getSuccessUrl());
            body.put("cancel_url", request.getFailUrl());
            body.put("fail_url", request.getFailUrl());

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return PaymentPrepareResponse.builder()
                        .paymentKey((String) responseBody.get("tid"))
                        .orderId(request.getOrderId())
                        .checkoutUrl((String) responseBody.get("next_redirect_pc_url"))
                        .amount(request.getAmount())
                        .build();
            }

            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        } catch (Exception e) {
            log.error("Kakao payment prepare failed", e);
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }
    }

    @Override
    public PaymentApprovalResponse approve(PaymentApprovalRequest request) {
        try {
            String url = apiUrl + "/v1/payment/approve";

            Map<String, Object> body = new HashMap<>();
            body.put("cid", "TC0ONETIME");
            body.put("tid", request.getPaymentKey());
            body.put("partner_order_id", request.getOrderId());
            body.put("partner_user_id", "USER_ID");
            body.put("pg_token", "PG_TOKEN_FROM_CALLBACK");

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> amount = (Map<String, Object>) responseBody.get("amount");

                return PaymentApprovalResponse.builder()
                        .paymentKey((String) responseBody.get("tid"))
                        .orderId((String) responseBody.get("partner_order_id"))
                        .transactionId((String) responseBody.get("aid"))
                        .amount(((Number) amount.get("total")).longValue())
                        .status("DONE")
                        .approvedAt(LocalDateTime.now())
                        .build();
            }

            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        } catch (Exception e) {
            log.error("Kakao payment approval failed", e);
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }
    }

    @Override
    public PaymentCancelResponse cancel(PaymentCancelRequest request) {
        try {
            String url = apiUrl + "/v1/payment/cancel";

            Map<String, Object> body = new HashMap<>();
            body.put("cid", "TC0ONETIME");
            body.put("tid", request.getPaymentKey());
            body.put("cancel_amount", request.getCancelAmount());
            body.put("cancel_tax_free_amount", 0);

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> canceledAmount = (Map<String, Object>) responseBody.get("canceled_amount");

                return PaymentCancelResponse.builder()
                        .paymentKey((String) responseBody.get("tid"))
                        .orderId((String) responseBody.get("partner_order_id"))
                        .transactionId((String) responseBody.get("aid"))
                        .cancelAmount(((Number) canceledAmount.get("total")).longValue())
                        .cancelReason(request.getCancelReason())
                        .canceledAt(LocalDateTime.now())
                        .build();
            }

            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        } catch (Exception e) {
            log.error("Kakao payment cancel failed", e);
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}