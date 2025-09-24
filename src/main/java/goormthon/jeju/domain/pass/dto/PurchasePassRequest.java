package goormthon.jeju.domain.pass.dto;

import goormthon.jeju.domain.pass.entity.PassType;
import goormthon.jeju.domain.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePassRequest {

    @NotNull(message = "정기권 타입은 필수입니다.")
    private PassType passType;

    @NotNull(message = "결제 방법은 필수입니다.")
    private PaymentMethod paymentMethod;
}