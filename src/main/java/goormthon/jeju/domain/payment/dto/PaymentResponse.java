package goormthon.jeju.domain.payment.dto;

import goormthon.jeju.domain.payment.entity.Payment;
import goormthon.jeju.domain.payment.entity.PaymentMethod;
import goormthon.jeju.domain.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Integer amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private String createdAt;

    public static PaymentResponse from(Payment payment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getCreatedAt().format(formatter)
        );
    }
}