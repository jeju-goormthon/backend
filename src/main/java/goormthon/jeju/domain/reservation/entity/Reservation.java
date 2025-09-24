package goormthon.jeju.domain.reservation.entity;

import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String reservationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MedicalDepartment medicalDepartment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column
    private boolean boarded;

    @Column(length = 500)
    private String qrCode;

    @Builder
    public Reservation(User user, Route route, LocalDate reservationDate, MedicalDepartment medicalDepartment) {
        this.reservationNumber = generateReservationNumber();
        this.user = user;
        this.route = route;
        this.reservationDate = reservationDate;
        this.medicalDepartment = medicalDepartment;
        this.status = ReservationStatus.CONFIRMED;
        this.boarded = false;
        this.qrCode = generateQRCode();
    }

    private String generateReservationNumber() {
        return UUID.randomUUID().toString().substring(0, 9).replace("-", "").toUpperCase();
    }

    private String generateQRCode() {
        return "QR_" + UUID.randomUUID().toString();
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public void board() {
        this.boarded = true;
        this.status = ReservationStatus.COMPLETED;
    }

    public void updateMedicalDepartment(MedicalDepartment medicalDepartment) {
        this.medicalDepartment = medicalDepartment;
    }

    public boolean canCancel() {
        return this.status == ReservationStatus.CONFIRMED && !this.boarded;
    }

    public boolean canUpdateDepartment() {
        return !this.boarded;
    }
}