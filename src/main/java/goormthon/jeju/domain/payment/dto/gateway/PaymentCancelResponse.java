package goormthon.jeju.domain.payment.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PaymentCancelResponse {
    private String paymentKey;
    private String orderId;
    private String transactionId;
    private Long cancelAmount;
    private String cancelReason;
    private LocalDateTime canceledAt;
}