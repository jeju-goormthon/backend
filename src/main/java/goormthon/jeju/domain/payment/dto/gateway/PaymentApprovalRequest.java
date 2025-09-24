package goormthon.jeju.domain.payment.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentApprovalRequest {
    private String paymentKey;
    private String orderId;
    private Long amount;
}