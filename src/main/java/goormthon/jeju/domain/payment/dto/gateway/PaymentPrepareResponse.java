package goormthon.jeju.domain.payment.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentPrepareResponse {
    private String paymentKey;
    private String orderId;
    private String checkoutUrl;
    private Long amount;
}