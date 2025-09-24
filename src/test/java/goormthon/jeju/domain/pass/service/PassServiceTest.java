package goormthon.jeju.domain.pass.service;

import goormthon.jeju.domain.pass.entity.Pass;
import goormthon.jeju.domain.pass.entity.PassStatus;
import goormthon.jeju.domain.pass.entity.PassType;
import goormthon.jeju.domain.pass.repository.PassRepository;
import goormthon.jeju.domain.user.entity.LoginType;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassServiceTest {

    @Mock
    private PassRepository passRepository;

    @InjectMocks
    private PassService passService;

    @Test
    @DisplayName("정기권 생성 성공")
    void createPass_Success() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        goormthon.jeju.domain.payment.entity.Payment payment = goormthon.jeju.domain.payment.entity.Payment.builder()
                .user(user)
                .amount(30000)
                .paymentMethod(goormthon.jeju.domain.payment.entity.PaymentMethod.TOSS_PAY)
                .build();

        Pass pass = Pass.builder()
                .user(user)
                .passType(PassType.ONE_MONTH)
                .startDate(java.time.LocalDateTime.now())
                .price(PassType.ONE_MONTH.getPrice())
                .payment(payment)
                .build();

        when(passRepository.existsByUserAndStatus(user, PassStatus.ACTIVE)).thenReturn(false);
        when(passRepository.save(any(Pass.class))).thenReturn(pass);

        Pass createdPass = passService.createPass(user, PassType.ONE_MONTH, payment);

        assertThat(createdPass).isNotNull();
        assertThat(createdPass.getPassType()).isEqualTo(PassType.ONE_MONTH);
        assertThat(createdPass.getStatus()).isEqualTo(PassStatus.ACTIVE);
        verify(passRepository, times(1)).save(any(Pass.class));
    }

    @Test
    @DisplayName("활성 정기권 보유 여부 확인")
    void hasActivePass_Success() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        when(passRepository.existsByUserAndStatus(user, PassStatus.ACTIVE)).thenReturn(true);

        boolean hasActivePass = passService.hasActivePass(user);

        assertThat(hasActivePass).isTrue();
        verify(passRepository, times(1)).existsByUserAndStatus(user, PassStatus.ACTIVE);
    }
}