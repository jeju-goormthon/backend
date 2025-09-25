package goormthon.jeju.domain.payment.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import goormthon.jeju.domain.payment.dto.gateway.*;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TossPaymentGatewayTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TossPaymentGateway tossPaymentGateway;

    private final String testSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private final String testApiUrl = "https://api.tosspayments.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tossPaymentGateway, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(tossPaymentGateway, "apiUrl", testApiUrl);
    }

    @Test
    @DisplayName("결제 승인 성공 - confirm 메서드")
    void confirm_Success() {
        // Given
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                .paymentKey("test_payment_key_123")
                .orderId("test_order_123")
                .amount(15000L)
                .build();

        Map<String, Object> mockResponseBody = new HashMap<>();
        mockResponseBody.put("paymentKey", "test_payment_key_123");
        mockResponseBody.put("orderId", "test_order_123");
        mockResponseBody.put("transactionKey", "test_transaction_123");
        mockResponseBody.put("totalAmount", 15000);
        mockResponseBody.put("status", "DONE");
        mockResponseBody.put("approvedAt", "2023-01-01T10:05:40+09:00");

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq(testApiUrl + "/v1/payments/confirm"),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(mockResponse);

        // When
        PaymentApprovalResponse response = tossPaymentGateway.confirm(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentKey()).isEqualTo("test_payment_key_123");
        assertThat(response.getOrderId()).isEqualTo("test_order_123");
        assertThat(response.getTransactionId()).isEqualTo("test_transaction_123");
        assertThat(response.getAmount()).isEqualTo(15000L);
        assertThat(response.getStatus()).isEqualTo("DONE");
        assertThat(response.getApprovedAt()).isNotNull();
    }

    @Test
    @DisplayName("결제 승인 실패 - API 응답 오류")
    void confirm_Fail_ApiError() {
        // Given
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                .paymentKey("test_payment_key_123")
                .orderId("test_order_123")
                .amount(15000L)
                .build();

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RestClientException("API Error"));

        // When & Then
        assertThatThrownBy(() -> tossPaymentGateway.confirm(request))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_FAILED);
    }

    @Test
    @DisplayName("결제 승인 실패 - HTTP 상태 코드 오류")
    void confirm_Fail_BadStatusCode() {
        // Given
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                .paymentKey("test_payment_key_123")
                .orderId("test_order_123")
                .amount(15000L)
                .build();

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(mockResponse);

        // When & Then
        assertThatThrownBy(() -> tossPaymentGateway.confirm(request))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_FAILED);
    }

    @Test
    @DisplayName("결제 취소 성공")
    void cancel_Success() {
        // Given
        PaymentCancelRequest request = PaymentCancelRequest.builder()
                .paymentKey("test_payment_key_123")
                .cancelReason("사용자 요청")
                .cancelAmount(15000L)
                .build();

        Map<String, Object> mockResponseBody = new HashMap<>();
        mockResponseBody.put("paymentKey", "test_payment_key_123");
        mockResponseBody.put("orderId", "test_order_123");
        mockResponseBody.put("transactionKey", "test_transaction_123");
        mockResponseBody.put("cancelAmount", 15000);
        mockResponseBody.put("cancelReason", "사용자 요청");

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq(testApiUrl + "/v1/payments/test_payment_key_123/cancel"),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(mockResponse);

        // When
        PaymentCancelResponse response = tossPaymentGateway.cancel(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentKey()).isEqualTo("test_payment_key_123");
        assertThat(response.getCancelAmount()).isEqualTo(15000L);
        assertThat(response.getCancelReason()).isEqualTo("사용자 요청");
        assertThat(response.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("Basic 인증 헤더 생성 확인")
    void createHeaders_BasicAuth() {
        // Given - When
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                .paymentKey("test_payment_key_123")
                .orderId("test_order_123")
                .amount(15000L)
                .build();

        Map<String, Object> mockResponseBody = new HashMap<>();
        mockResponseBody.put("paymentKey", "test_payment_key_123");
        mockResponseBody.put("orderId", "test_order_123");
        mockResponseBody.put("transactionKey", "test_transaction_123");
        mockResponseBody.put("totalAmount", 15000);
        mockResponseBody.put("status", "DONE");

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(mockResponse);

        tossPaymentGateway.confirm(request);

        // Then - 헤더 확인
        verify(restTemplate).postForEntity(
                anyString(),
                argThat(entity -> {
                    HttpHeaders headers = ((HttpEntity<?>) entity).getHeaders();
                    String authHeader = headers.getFirst("Authorization");
                    String expectedAuth = "Basic " + Base64.getEncoder()
                            .encodeToString((testSecretKey + ":").getBytes(StandardCharsets.UTF_8));

                    return authHeader != null &&
                           authHeader.equals(expectedAuth) &&
                           MediaType.APPLICATION_JSON.equals(headers.getContentType());
                }),
                eq(Map.class)
        );
    }

    @Test
    @DisplayName("결제 정보 조회 성공")
    void getPayment_Success() {
        // Given
        String paymentKey = "test_payment_key_123";

        Map<String, Object> mockResponseBody = new HashMap<>();
        mockResponseBody.put("paymentKey", paymentKey);
        mockResponseBody.put("orderId", "test_order_123");
        mockResponseBody.put("totalAmount", 15000);
        mockResponseBody.put("status", "DONE");

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(testApiUrl + "/v1/payments/" + paymentKey),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(mockResponse);

        // When
        Map<String, Object> response = tossPaymentGateway.getPayment(paymentKey);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get("paymentKey")).isEqualTo(paymentKey);
        assertThat(response.get("status")).isEqualTo("DONE");
    }
}