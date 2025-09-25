package goormthon.jeju.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmResponse {

    private String paymentKey;
    private String orderId;
    private String transactionId;
    private Long amount;
    private String status;
    private LocalDateTime approvedAt;
}