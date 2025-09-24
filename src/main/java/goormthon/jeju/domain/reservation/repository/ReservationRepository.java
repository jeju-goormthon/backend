package goormthon.jeju.domain.reservation.repository;

import goormthon.jeju.domain.reservation.entity.Reservation;
import goormthon.jeju.domain.reservation.entity.ReservationStatus;
import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserOrderByReservationDateDesc(User user);
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    Optional<Reservation> findByQrCode(String qrCode);
    boolean existsByUserAndRouteAndReservationDateAndStatus(User user, Route route, LocalDate reservationDate, ReservationStatus status);
    List<Reservation> findByUserAndReservationDateAndStatusIn(User user, LocalDate reservationDate, List<ReservationStatus> statuses);
}