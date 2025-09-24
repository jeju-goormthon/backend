package goormthon.jeju.domain.payment.service;

import goormthon.jeju.domain.payment.dto.gateway.PaymentPrepareRequest;
import goormthon.jeju.domain.payment.dto.gateway.PaymentPrepareResponse;
import goormthon.jeju.domain.payment.entity.Payment;
import goormthon.jeju.domain.payment.entity.PaymentMethod;
import goormthon.jeju.domain.payment.entity.PaymentStatus;
import goormthon.jeju.domain.payment.gateway.PaymentGateway;
import goormthon.jeju.domain.payment.repository.PaymentRepository;
import goormthon.jeju.domain.user.entity.LoginType;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Map<String, PaymentGateway> paymentGateways;

    @Mock
    private PaymentGateway mockGateway;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("결제 준비 성공")
    void preparePayment_Success() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        Payment payment = Payment.builder()
                .user(user)
                .amount(30000)
                .paymentMethod(PaymentMethod.TOSS_PAY)
                .build();

        PaymentPrepareResponse mockResponse = PaymentPrepareResponse.builder()
                .paymentKey("test_payment_key")
                .orderId("1234567890")
                .checkoutUrl("https://checkout.url")
                .amount(30000L)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentGateways.get("tossPaymentGateway")).thenReturn(mockGateway);
        when(mockGateway.prepare(any(PaymentPrepareRequest.class))).thenReturn(mockResponse);

        PaymentPrepareResponse response = paymentService.preparePayment(
                user,
                30000L,
                PaymentMethod.TOSS_PAY,
                "1234567890",
                "정기권 구매"
        );

        assertThat(response).isNotNull();
        assertThat(response.getPaymentKey()).isEqualTo("test_payment_key");
        assertThat(response.getOrderId()).isEqualTo("1234567890");
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제 완료 처리 성공")
    void completePaymentByOrderId_Success() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        Payment payment = Payment.builder()
                .user(user)
                .amount(30000)
                .paymentMethod(PaymentMethod.TOSS_PAY)
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        paymentService.completePaymentByOrderId("1", "transaction_123");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getTransactionId()).isEqualTo("transaction_123");
    }

    @Test
    @DisplayName("결제 실패 처리 성공")
    void failPaymentByOrderId_Success() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        Payment payment = Payment.builder()
                .user(user)
                .amount(30000)
                .paymentMethod(PaymentMethod.TOSS_PAY)
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        paymentService.failPaymentByOrderId("1");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("결제 취소 실패 - 취소 불가능한 상태")
    void cancelPayment_Fail_CannotCancel() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        Payment payment = Payment.builder()
                .user(user)
                .amount(30000)
                .paymentMethod(PaymentMethod.TOSS_PAY)
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.cancelPayment(1L, "사용자 요청"))
                .isInstanceOf(GlobalException.class);
    }
}