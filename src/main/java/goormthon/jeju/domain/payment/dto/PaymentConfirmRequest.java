package goormthon.jeju.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {

    @NotBlank
    private String paymentKey;

    @NotBlank
    private String orderId;

    @NotNull
    @Positive
    private Long amount;
}