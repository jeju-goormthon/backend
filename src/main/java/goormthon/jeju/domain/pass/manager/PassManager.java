package goormthon.jeju.domain.pass.manager;

import goormthon.jeju.domain.pass.dto.PassResponse;
import goormthon.jeju.domain.pass.entity.Pass;
import goormthon.jeju.domain.pass.entity.PassType;
import goormthon.jeju.domain.pass.service.PassService;
import goormthon.jeju.domain.payment.entity.Payment;
import goormthon.jeju.domain.payment.entity.PaymentMethod;
import goormthon.jeju.domain.payment.service.PaymentService;
import goormthon.jeju.domain.payment.dto.gateway.PaymentPrepareResponse;
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

    /**
     * 정기권 구매 준비 (결제 전)
     * Payment를 생성하고 결제 준비 정보를 반환합니다.
     */
    @Transactional
    public PaymentPrepareResponse preparePurchase(Long userId, PassType passType, PaymentMethod paymentMethod) {
        User user = userService.findById(userId);

        // 활성 정기권 중복 체크
        if (passService.hasActivePass(user)) {
            throw new GlobalException(ErrorCode.PASS_ALREADY_EXISTS);
        }

        String orderId = "PASS_" + System.currentTimeMillis();
        String orderName = passType.name() + " 정기권";

        try {
            return paymentService.preparePayment(
                    user,
                    (long) passType.getPrice(),
                    paymentMethod,
                    orderId,
                    orderName
            );
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }
    }

    /**
     * @deprecated 결제 전 정기권 생성으로 인한 데이터 무결성 문제로 사용 중단
     * 대신 preparePurchase() 사용 권장
     */
    @Deprecated
    @Transactional
    public PassResponse purchasePass(Long userId, PassType passType, PaymentMethod paymentMethod) {
        User user = userService.findById(userId);

        String orderId = String.valueOf(System.currentTimeMillis());

        Payment payment = new Payment(user, passType.getPrice(), paymentMethod, orderId);

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