package goormthon.jeju.domain.route.service;

import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.route.repository.RouteRepository;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RouteService routeService;

    @Test
    @DisplayName("좌석 증가 성공")
    void incrementBookedSeats_Success() {
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

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        routeService.incrementBookedSeats(1L);

        assertThat(route.getBookedSeats()).isEqualTo(6);
    }

    @Test
    @DisplayName("좌석 감소 성공")
    void decrementBookedSeats_Success() {
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

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        routeService.decrementBookedSeats(1L);

        assertThat(route.getBookedSeats()).isEqualTo(4);
    }
}