package goormthon.jeju.domain.payment.gateway;

import goormthon.jeju.domain.payment.dto.gateway.*;

public interface PaymentGateway {

    PaymentPrepareResponse prepare(PaymentPrepareRequest request);

    PaymentApprovalResponse approve(PaymentApprovalRequest request);

    PaymentCancelResponse cancel(PaymentCancelRequest request);
}