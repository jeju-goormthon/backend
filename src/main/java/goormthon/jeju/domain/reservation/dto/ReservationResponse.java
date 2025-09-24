package goormthon.jeju.domain.reservation.dto;

import goormthon.jeju.domain.reservation.entity.Reservation;
import goormthon.jeju.domain.reservation.entity.ReservationStatus;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private String reservationNumber;
    private String reservationDate;
    private String hospitalName;
    private String startTime;
    private String pickupLocation;
    private MedicalDepartment medicalDepartment;
    private ReservationStatus status;
    private boolean boarded;
    private String qrCode;

    public static ReservationResponse from(Reservation reservation) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return new ReservationResponse(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getReservationDate().format(dateFormatter),
                reservation.getRoute().getHospitalName(),
                reservation.getRoute().getStartTime().format(timeFormatter),
                reservation.getRoute().getPickupLocation(),
                reservation.getMedicalDepartment(),
                reservation.getStatus(),
                reservation.isBoarded(),
                reservation.getQrCode()
        );
    }
}