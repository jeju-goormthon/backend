package goormthon.jeju.domain.payment.controller;

import goormthon.jeju.domain.payment.controller.spec.PaymentControllerSpec;
import goormthon.jeju.domain.payment.dto.PaymentResponse;
import goormthon.jeju.domain.payment.manager.PaymentManager;
import goormthon.jeju.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerSpec {

    private final PaymentManager paymentManager;

    @GetMapping
    public ApiResponse<List<PaymentResponse>> getMyPayments(@AuthenticationPrincipal Long userId) {
        List<PaymentResponse> payments = paymentManager.getMyPayments(userId);
        return ApiResponse.success(payments);
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<PaymentResponse> getPaymentDetail(@PathVariable Long paymentId) {
        PaymentResponse payment = paymentManager.getPaymentDetail(paymentId);
        return ApiResponse.success(payment);
    }
}