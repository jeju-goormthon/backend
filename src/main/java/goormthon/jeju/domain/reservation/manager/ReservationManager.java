package goormthon.jeju.domain.reservation.manager;

import goormthon.jeju.domain.pass.service.PassService;
import goormthon.jeju.domain.reservation.dto.ReservationResponse;
import goormthon.jeju.domain.reservation.entity.Reservation;
import goormthon.jeju.domain.reservation.service.ReservationNotificationService;
import goormthon.jeju.domain.reservation.service.ReservationService;
import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.route.service.RouteService;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationManager {

    private final ReservationService reservationService;
    private final RouteService routeService;
    private final UserService userService;
    private final PassService passService;
    private final ReservationNotificationService notificationService;

    @Transactional
    public ReservationResponse createReservation(Long userId, Long routeId, LocalDate reservationDate) {
        User user = userService.findById(userId);
        Route route = routeService.findById(routeId);

//        boolean hasActivePass = passService.hasActivePass(user);
//        if (!hasActivePass) {
//            routeService.incrementBookedSeats(routeId);
//        }
        routeService.incrementBookedSeats(routeId);

        Reservation reservation = Reservation.builder()
                .user(user)
                .route(route)
                .reservationDate(reservationDate)
                .medicalDepartment(user.getMedicalDepartment())
                .build();

        Reservation savedReservation = reservationService.createReservation(reservation);
        notificationService.sendReservationConfirmation(savedReservation);
        return ReservationResponse.from(savedReservation);
    }

    public List<ReservationResponse> getMyReservations(Long userId) {
        User user = userService.findById(userId);
        List<Reservation> reservations = reservationService.getReservationsByUser(user);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ReservationResponse getReservationDetail(Long reservationId) {
        Reservation reservation = reservationService.findById(reservationId);
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationService.findById(reservationId);

        if (!reservation.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 예약만 취소할 수 있습니다.");
        }

        reservationService.cancelReservation(reservationId);
        routeService.decrementBookedSeats(reservation.getRoute().getId());
    }

    @Transactional
    public void boardReservation(String qrCode) {
        reservationService.boardReservation(qrCode);
    }
}