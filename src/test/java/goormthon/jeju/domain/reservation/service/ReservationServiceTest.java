package goormthon.jeju.domain.reservation.service;

import goormthon.jeju.domain.reservation.entity.Reservation;
import goormthon.jeju.domain.reservation.entity.ReservationStatus;
import goormthon.jeju.domain.reservation.repository.ReservationRepository;
import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.user.entity.LoginType;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("예약 생성 성공")
    void createReservation_Success() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        Route route = Route.builder()
                .hospitalName("제주대병원")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .expectedMinutes(60)
                .totalSeats(20)
                .bookedSeats(5)
                .pickupLocation("제주시청")
                .build();

        Reservation reservation = Reservation.builder()
                .user(user)
                .route(route)
                .reservationDate(LocalDate.now().plusDays(1))
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .build();

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation savedReservation = reservationService.createReservation(reservation);

        assertThat(savedReservation).isNotNull();
        assertThat(savedReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("예약 취소 성공")
    void cancelReservation_Success() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        Route route = Route.builder()
                .hospitalName("제주대병원")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .expectedMinutes(60)
                .totalSeats(20)
                .bookedSeats(5)
                .pickupLocation("제주시청")
                .build();

        Reservation reservation = Reservation.builder()
                .user(user)
                .route(route)
                .reservationDate(LocalDate.now().plusDays(1))
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .build();

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(1L);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }
}