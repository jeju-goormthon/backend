package goormthon.jeju.domain.pass.manager;

import goormthon.jeju.domain.pass.dto.PassResponse;
import goormthon.jeju.domain.pass.entity.Pass;
import goormthon.jeju.domain.pass.entity.PassType;
import goormthon.jeju.domain.pass.service.PassService;
import goormthon.jeju.domain.payment.entity.Payment;
import goormthon.jeju.domain.payment.entity.PaymentMethod;
import goormthon.jeju.domain.payment.service.PaymentService;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.service.UserService;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassManager {

    private final PassService passService;
    private final PaymentService paymentService;
    private final UserService userService;

    @Transactional
    public PassResponse purchasePass(Long userId, PassType passType, PaymentMethod paymentMethod) {
        User user = userService.findById(userId);

        String orderId = String.valueOf(System.currentTimeMillis());
        String orderName = passType.name() + " 정기권";

        Payment payment;
        try {
            payment = paymentService.createPayment(
                    user,
                    (long) passType.getPrice(),
                    paymentMethod,
                    orderId,
                    orderName
            );
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }

        Pass pass = passService.createPass(user, passType, payment);
        return PassResponse.from(pass);
    }

    public List<PassResponse> getMyPasses(Long userId) {
        User user = userService.findById(userId);
        List<Pass> passes = passService.getPassesByUser(user);
        return passes.stream()
                .map(PassResponse::from)
                .toList();
    }

    public PassResponse getActivePass(Long userId) {
        User user = userService.findById(userId);
        Pass pass = passService.getActivePass(user);
        return PassResponse.from(pass);
    }

    public boolean hasActivePass(Long userId) {
        User user = userService.findById(userId);
        return passService.hasActivePass(user);
    }

    @Transactional
    public void cancelPass(Long userId, Long passId) {
        User user = userService.findById(userId);
        Pass pass = passService.findById(passId);

        if (!pass.getUser().getId().equals(user.getId())) {
            throw new GlobalException(ErrorCode.PASS_NOT_FOUND);
        }

        if (pass.getPayment() != null && pass.getPayment().canCancel()) {
            paymentService.cancelPayment(pass.getPayment().getId(), "정기권 취소");
        }

        passService.cancelPass(passId);
    }
}