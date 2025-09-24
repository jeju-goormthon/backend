package goormthon.jeju.domain.payment.manager;

import goormthon.jeju.domain.payment.dto.PaymentResponse;
import goormthon.jeju.domain.payment.entity.Payment;
import goormthon.jeju.domain.payment.service.PaymentService;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.service.UserService;
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
}