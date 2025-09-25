package goormthon.jeju.domain.payment.repository;

import goormthon.jeju.domain.payment.entity.Payment;
import goormthon.jeju.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserOrderByCreatedAtDesc(User user);
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByOrderId(String orderId);
}