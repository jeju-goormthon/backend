package goormthon.jeju.domain.route.entity;

import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "routes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Route extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String hospitalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MedicalDepartment medicalDepartment;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer expectedMinutes;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer bookedSeats;

    @Column(nullable = false, length = 100)
    private String pickupLocation;

    @Builder
    public Route(String hospitalName, MedicalDepartment medicalDepartment, LocalTime startTime,
                 LocalTime endTime, Integer expectedMinutes, Integer totalSeats,
                 Integer bookedSeats, String pickupLocation) {
        this.hospitalName = hospitalName;
        this.medicalDepartment = medicalDepartment;
        this.startTime = startTime;
        this.endTime = endTime;
        this.expectedMinutes = expectedMinutes;
        this.totalSeats = totalSeats;
        this.bookedSeats = bookedSeats;
        this.pickupLocation = pickupLocation;
    }

    public void incrementBookedSeats() {
        this.bookedSeats++;
    }

    public void decrementBookedSeats() {
        if (this.bookedSeats > 0) {
            this.bookedSeats--;
        }
    }

    public boolean hasAvailableSeats() {
        return this.bookedSeats < this.totalSeats;
    }

    public Integer getRemainedSeats() {
        return this.totalSeats - this.bookedSeats;
    }
}