package goormthon.jeju.domain.route.dto;

import goormthon.jeju.domain.route.entity.Route;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class RouteResponse {
    private Long id;
    private String hospitalName;
    private String startAt;
    private String endAt;
    private Integer expectedTime;
    private Integer remainedSeat;
    private Integer totalSeat;
    private String pickupLocation;

    public static RouteResponse from(Route route) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return new RouteResponse(
                route.getId(),
                route.getHospitalName(),
                route.getStartTime().format(formatter),
                route.getEndTime().format(formatter),
                route.getExpectedMinutes(),
                route.getRemainedSeats(),
                route.getTotalSeats(),
                route.getPickupLocation()
        );
    }
}