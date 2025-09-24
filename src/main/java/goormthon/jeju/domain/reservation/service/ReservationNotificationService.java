package goormthon.jeju.domain.reservation.service;

import goormthon.jeju.domain.reservation.entity.Reservation;
import goormthon.jeju.domain.reservation.entity.ReservationStatus;
import goormthon.jeju.domain.reservation.repository.ReservationRepository;
import goormthon.jeju.domain.verification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationNotificationService {

    private final ReservationRepository reservationRepository;
    private final SmsService smsService;

    public void sendReservationConfirmation(Reservation reservation) {
        String message = String.format(
                "[제주 셔틀] 예약이 완료되었습니다.\n" +
                "예약번호: %s\n" +
                "노선: %s\n" +
                "예약일: %s\n" +
                "출발시간: %s",
                reservation.getReservationNumber(),
                reservation.getRoute().getHospitalName(),
                reservation.getReservationDate(),
                reservation.getRoute().getStartTime()
        );

        smsService.sendSms(reservation.getUser().getPhoneNumber(), message);
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void sendBoardingReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusMinutes(30);

        LocalDate targetDate = reminderTime.toLocalDate();
        LocalTime startTime = reminderTime.toLocalTime().minusMinutes(5);
        LocalTime endTime = reminderTime.toLocalTime().plusMinutes(5);

        List<Reservation> upcomingReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .filter(r -> r.getReservationDate().equals(targetDate))
                .filter(r -> {
                    LocalTime routeTime = r.getRoute().getStartTime();
                    return routeTime.isAfter(startTime) && routeTime.isBefore(endTime);
                })
                .toList();

        for (Reservation reservation : upcomingReservations) {
            String message = String.format(
                    "[제주 셔틀] 30분 후 출발 예정입니다.\n" +
                    "예약번호: %s\n" +
                    "탑승위치: %s\n" +
                    "출발시간: %s",
                    reservation.getReservationNumber(),
                    reservation.getRoute().getPickupLocation(),
                    reservation.getRoute().getStartTime()
            );

            smsService.sendSms(reservation.getUser().getPhoneNumber(), message);
            log.info("Boarding reminder sent to user {} for reservation {}",
                    reservation.getUser().getPhoneNumber(),
                    reservation.getReservationNumber());
        }
    }
}