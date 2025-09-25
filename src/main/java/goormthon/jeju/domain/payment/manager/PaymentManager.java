package goormthon.jeju.domain.payment.manager;

import goormthon.jeju.domain.payment.dto.PaymentConfirmRequest;
import goormthon.jeju.domain.payment.dto.PaymentConfirmResponse;
import goormthon.jeju.domain.payment.dto.PaymentResponse;
import goormthon.jeju.domain.payment.dto.gateway.PaymentApprovalRequest;
import goormthon.jeju.domain.payment.dto.gateway.PaymentApprovalResponse;
import goormthon.jeju.domain.payment.entity.Payment;
import goormthon.jeju.domain.payment.entity.PaymentMethod;
import goormthon.jeju.domain.payment.gateway.TossPaymentGateway;
import goormthon.jeju.domain.payment.service.PaymentService;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.service.UserService;
import goormthon.jeju.domain.pass.service.PassService;
import goormthon.jeju.domain.pass.entity.PassType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentManager {

    private final PaymentService paymentService;
    private final UserService userService;
    private final TossPaymentGateway tossPaymentGateway;
    private final PassService passService;

    public List<PaymentResponse> getMyPayments(Long userId) {
        User user = userService.findById(userId);
        List<Payment> payments = paymentService.getPaymentsByUser(user);
        return payments.stream()
                .map(PaymentResponse::from)
                .toList();
    }

    public PaymentResponse getPaymentDetail(Long paymentId) {
        Payment payment = paymentService.findById(paymentId);
        return PaymentResponse.from(payment);
    }

    /**
     * 토스페이먼츠 결제 승인
     * 프론트엔드에서 결제 완료 후 서버에서 최종 승인을 처리합니다.
     */
    @Transactional
    public PaymentConfirmResponse confirmPayment(Long userId, PaymentConfirmRequest request) {
        // 사용자 검증
        User user = userService.findById(userId);

        // 주문 ID로 결제 정보 조회
        Payment payment = paymentService.findByOrderId(request.getOrderId());

        // 결제 승인 요청
        PaymentApprovalRequest approvalRequest = PaymentApprovalRequest.builder()
                .paymentKey(request.getPaymentKey())
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .build();

        PaymentApprovalResponse approvalResponse = tossPaymentGateway.confirm(approvalRequest);

        // 결제 완료 처리
        paymentService.completePaymentByOrderId(
                request.getOrderId(),
                approvalResponse.getTransactionId()
        );

        return PaymentConfirmResponse.builder()
                .paymentKey(approvalResponse.getPaymentKey())
                .orderId(approvalResponse.getOrderId())
                .transactionId(approvalResponse.getTransactionId())
                .amount(approvalResponse.getAmount())
                .status(approvalResponse.getStatus())
                .approvedAt(approvalResponse.getApprovedAt())
                .build();
    }

    /**
     * 결제 완료 후 정기권 생성
     * 정기권 구매 전용 결제 확인 메서드
     */
    @Transactional
    public PaymentConfirmResponse confirmPaymentAndCreatePass(Long userId, PaymentConfirmRequest request, PassType passType) {
        // 결제 승인
        PaymentConfirmResponse confirmResponse = confirmPayment(userId, request);

        // 결제 완료된 Payment 조회
        Payment completedPayment = paymentService.findByOrderId(request.getOrderId());

        // 결제 완료 후 정기권 생성
        passService.createPassAfterPayment(completedPayment, passType);

        return confirmResponse;
    }
}