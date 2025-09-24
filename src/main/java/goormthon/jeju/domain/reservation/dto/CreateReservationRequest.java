package goormthon.jeju.domain.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {

    @NotNull(message = "노선 ID는 필수입니다.")
    private Long routeId;

    @NotNull(message = "예약 날짜는 필수입니다.")
    private LocalDate reservationDate;
}