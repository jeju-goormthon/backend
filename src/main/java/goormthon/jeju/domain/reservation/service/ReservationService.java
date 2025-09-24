package goormthon.jeju.domain.reservation.service;

import goormthon.jeju.domain.reservation.entity.Reservation;
import goormthon.jeju.domain.reservation.entity.ReservationStatus;
import goormthon.jeju.domain.reservation.repository.ReservationRepository;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        validateReservation(reservation);
        return reservationRepository.save(reservation);
    }

    private void validateReservation(Reservation reservation) {
        if (reservation.getReservationDate().isBefore(LocalDate.now())) {
            throw new GlobalException(ErrorCode.PAST_DATE_RESERVATION);
        }

        if (reservationRepository.existsByUserAndRouteAndReservationDateAndStatus(
                reservation.getUser(),
                reservation.getRoute(),
                reservation.getReservationDate(),
                ReservationStatus.CONFIRMED
        )) {
            throw new GlobalException(ErrorCode.DUPLICATE_RESERVATION);
        }

        List<Reservation> existingReservations = reservationRepository.findByUserAndReservationDateAndStatusIn(
                reservation.getUser(),
                reservation.getReservationDate(),
                List.of(ReservationStatus.CONFIRMED, ReservationStatus.COMPLETED)
        );

        for (Reservation existing : existingReservations) {
            if (hasTimeConflict(reservation.getRoute(), existing.getRoute())) {
                throw new GlobalException(ErrorCode.TIME_CONFLICT_RESERVATION);
            }
        }
    }

    private boolean hasTimeConflict(goormthon.jeju.domain.route.entity.Route route1, goormthon.jeju.domain.route.entity.Route route2) {
        return route1.getStartTime().isBefore(route2.getEndTime()) && route2.getStartTime().isBefore(route1.getEndTime());
    }

    public List<Reservation> getReservationsByUser(User user) {
        return reservationRepository.findByUserOrderByReservationDateDesc(user);
    }

    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GlobalException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    public Reservation findByReservationNumber(String reservationNumber) {
        return reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new GlobalException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    public Reservation findByQrCode(String qrCode) {
        return reservationRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new GlobalException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = findById(reservationId);

        if (!reservation.canCancel()) {
            throw new GlobalException(ErrorCode.CANNOT_CANCEL_AFTER_BOARDING);
        }

        reservation.cancel();
    }

    @Transactional
    public void boardReservation(String qrCode) {
        Reservation reservation = findByQrCode(qrCode);

        if (reservation.isBoarded()) {
            throw new GlobalException(ErrorCode.ALREADY_BOARDED);
        }

        reservation.board();
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}