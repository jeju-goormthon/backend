package goormthon.jeju.domain.route.dto;

import goormthon.jeju.domain.user.entity.MedicalDepartment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class CreateRouteRequest {

    @NotBlank(message = "병원 이름은 필수입니다.")
    private String hospitalName;

    @NotNull(message = "진료과목은 필수입니다.")
    private MedicalDepartment medicalDepartment;

    @NotNull(message = "출발 시간은 필수입니다.")
    private LocalTime startTime;

    @NotNull(message = "도착 시간은 필수입니다.")
    private LocalTime endTime;

    @NotNull(message = "예상 소요 시간은 필수입니다.")
    private Integer expectedMinutes;

    @NotNull(message = "총 좌석 수는 필수입니다.")
    private Integer totalSeats;

    @NotBlank(message = "탑승 위치는 필수입니다.")
    private String pickupLocation;
}