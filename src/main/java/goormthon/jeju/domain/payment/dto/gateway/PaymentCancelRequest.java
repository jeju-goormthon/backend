package goormthon.jeju.domain.payment.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentCancelRequest {
    private String paymentKey;
    private String cancelReason;
    private Long cancelAmount;
}