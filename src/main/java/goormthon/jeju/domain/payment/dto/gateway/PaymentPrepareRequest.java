package goormthon.jeju.domain.payment.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentPrepareRequest {
    private String orderId;
    private String orderName;
    private Long amount;
    private String customerName;
    private String customerPhoneNumber;
    private String successUrl;
    private String failUrl;
}