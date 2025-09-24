package goormthon.jeju.domain.reservation.manager;

import goormthon.jeju.domain.reservation.dto.ReservationResponse;
import goormthon.jeju.domain.reservation.entity.Reservation;
import goormthon.jeju.domain.reservation.service.ReservationService;
import goormthon.jeju.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReservationManager {

    private final ReservationService reservationService;
    private final UserService userService;

    public List<ReservationResponse> getAllReservations(Long adminId) {
        userService.checkAdmin(adminId);
        List<Reservation> reservations = reservationService.getAllReservations();
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }
}