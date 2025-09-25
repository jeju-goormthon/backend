package goormthon.jeju.domain.payment.service;

import goormthon.jeju.domain.payment.dto.gateway.*;
import goormthon.jeju.domain.payment.entity.Payment;
import goormthon.jeju.domain.payment.entity.PaymentMethod;
import goormthon.jeju.domain.payment.gateway.PaymentGateway;
import goormthon.jeju.domain.payment.repository.PaymentRepository;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Map<String, PaymentGateway> paymentGateways;

    @Transactional
    public Payment createPayment(User user, Long amount, PaymentMethod paymentMethod, String orderId, String orderName) {
        Payment payment = Payment.builder()
                .user(user)
                .amount(amount.intValue())
                .paymentMethod(paymentMethod)
                .orderId(orderId)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        PaymentGateway gateway = getPaymentGateway(paymentMethod);

        PaymentPrepareRequest request = PaymentPrepareRequest.builder()
                .orderId(orderId)
                .orderName(orderName)
                .amount(amount)
                .customerName(user.getName() != null ? user.getName() : "고객")
                .customerPhoneNumber(user.getPhoneNumber())
                .successUrl("https://your-domain.com/payment/success")
                .failUrl("https://your-domain.com/payment/fail")
                .build();

        gateway.prepare(request);

        return savedPayment;
    }

    @Transactional
    public PaymentPrepareResponse preparePayment(User user, Long amount, PaymentMethod paymentMethod, String orderId, String orderName) {
        Payment payment = Payment.builder()
                .user(user)
                .amount(amount.intValue())
                .paymentMethod(paymentMethod)
                .orderId(orderId)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        PaymentGateway gateway = getPaymentGateway(paymentMethod);

        PaymentPrepareRequest request = PaymentPrepareRequest.builder()
                .orderId(orderId)
                .orderName(orderName)
                .amount(amount)
                .customerName(user.getName() != null ? user.getName() : "고객")
                .customerPhoneNumber(user.getPhoneNumber())
                .successUrl("https://your-domain.com/payment/success")
                .failUrl("https://your-domain.com/payment/fail")
                .build();

        return gateway.prepare(request);
    }

    @Transactional
    public void approvePayment(Long paymentId, String paymentKey, String orderId, Long amount) {
        Payment payment = findById(paymentId);

        PaymentGateway gateway = getPaymentGateway(payment.getPaymentMethod());

        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();

        PaymentApprovalResponse response = gateway.approve(request);
        payment.complete(response.getTransactionId());
    }

    @Transactional
    public void completePaymentByOrderId(String orderId, String transactionId) {
        Payment payment = findByOrderId(orderId);
        payment.complete(transactionId);
    }

    @Transactional
    public void cancelPayment(Long paymentId, String cancelReason) {
        Payment payment = findById(paymentId);

        if (!payment.canCancel()) {
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }

        PaymentGateway gateway = getPaymentGateway(payment.getPaymentMethod());

        PaymentCancelRequest request = PaymentCancelRequest.builder()
                .paymentKey(payment.getTransactionId())
                .cancelReason(cancelReason)
                .cancelAmount(payment.getAmount().longValue())
                .build();

        gateway.cancel(request);
        payment.fail();
    }

    @Transactional
    public void failPayment(Long paymentId) {
        Payment payment = findById(paymentId);
        payment.fail();
    }

    @Transactional
    public void failPaymentByOrderId(String orderId) {
        Payment payment = findByOrderId(orderId);
        payment.fail();
    }

    public Payment findById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new GlobalException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    public Payment findByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new GlobalException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByUserOrderByCreatedAtDesc(user);
    }

    private PaymentGateway getPaymentGateway(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case KAKAO_PAY -> paymentGateways.get("kakaoPaymentGateway");
            case TOSS_PAY -> paymentGateways.get("tossPaymentGateway");
        };
    }
}