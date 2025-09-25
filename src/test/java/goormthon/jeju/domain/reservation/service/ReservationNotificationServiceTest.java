package goormthon.jeju.domain.reservation.service;

import goormthon.jeju.domain.reservation.entity.Reservation;
import goormthon.jeju.domain.reservation.entity.ReservationStatus;
import goormthon.jeju.domain.reservation.repository.ReservationRepository;
import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.user.entity.LoginType;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.entity.UserRole;
import goormthon.jeju.domain.verification.service.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationNotificationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private ReservationNotificationService reservationNotificationService;

    private User userWithPhoneNumber;
    private User userWithoutPhoneNumber;
    private Route route;
    private Reservation reservationWithPhoneNumber;
    private Reservation reservationWithoutPhoneNumber;

    @BeforeEach
    void setUp() {
        userWithPhoneNumber = User.builder()
                .phoneNumber("01012345678")
                .name("테스트유저1")
                .email("test1@test.com")
                .loginType(LoginType.NORMAL)
                .role(UserRole.USER)
                .build();

        userWithoutPhoneNumber = User.builder()
                .phoneNumber(null)
                .name("테스트유저2")
                .email("test2@test.com")
                .loginType(LoginType.KAKAO)
                .role(UserRole.USER)
                .build();

        route = Route.builder()
                .hospitalName("제주대학교병원")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .pickupLocation("제주시청")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 30))
                .expectedMinutes(30)
                .totalSeats(20)
                .bookedSeats(0)
                .build();

        reservationWithPhoneNumber = Reservation.builder()
                .user(userWithPhoneNumber)
                .route(route)
                .reservationDate(LocalDate.now())
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .build();

        reservationWithoutPhoneNumber = Reservation.builder()
                .user(userWithoutPhoneNumber)
                .route(route)
                .reservationDate(LocalDate.now())
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .build();
    }

    @Test
    @DisplayName("전화번호가 있는 사용자에게는 예약 확인 SMS를 보낸다")
    void shouldSendConfirmationSmsWhenUserHasPhoneNumber() {
        // when
        reservationNotificationService.sendReservationConfirmation(reservationWithPhoneNumber);

        // then
        verify(smsService).sendSms(eq("01012345678"), anyString());
    }

    @Test
    @DisplayName("전화번호가 없는 사용자에게는 예약 확인 SMS를 보내지 않는다")
    void shouldNotSendConfirmationSmsWhenUserHasNoPhoneNumber() {
        // when
        assertDoesNotThrow(() -> reservationNotificationService.sendReservationConfirmation(reservationWithoutPhoneNumber));

        // then
        verify(smsService, never()).sendSms(any(), anyString());
    }

    @Test
    @DisplayName("스케줄러에서 전화번호가 없는 사용자는 탑승 알림 SMS를 받지 않는다")
    void shouldNotSendBoardingReminderWhenUserHasNoPhoneNumber() {
        // given
        when(reservationRepository.findAll()).thenReturn(List.of(reservationWithoutPhoneNumber));

        // when
        assertDoesNotThrow(() -> reservationNotificationService.sendBoardingReminders());

        // then
        verify(smsService, never()).sendSms(any(), anyString());
    }
}