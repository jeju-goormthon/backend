package goormthon.jeju.domain.payment.controller;

import goormthon.jeju.domain.payment.controller.spec.PaymentControllerSpec;
import goormthon.jeju.domain.payment.dto.PaymentConfirmRequest;
import goormthon.jeju.domain.payment.dto.PaymentConfirmResponse;
import goormthon.jeju.domain.payment.dto.PaymentResponse;
import goormthon.jeju.domain.payment.manager.PaymentManager;
import goormthon.jeju.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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

    /**
     * 토스페이먼츠 결제 승인
     * 프론트엔드에서 결제 완료 후 서버에서 최종 승인을 처리합니다.
     */
    @PostMapping("/confirm")
    public ApiResponse<PaymentConfirmResponse> confirmPayment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PaymentConfirmRequest request) {
        PaymentConfirmResponse response = paymentManager.confirmPayment(userId, request);
        return ApiResponse.success(response);
    }
}