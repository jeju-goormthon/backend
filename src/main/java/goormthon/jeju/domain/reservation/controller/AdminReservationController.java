package goormthon.jeju.domain.reservation.controller;

import goormthon.jeju.domain.reservation.dto.ReservationResponse;
import goormthon.jeju.domain.reservation.manager.AdminReservationManager;
import goormthon.jeju.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {

    private final AdminReservationManager adminReservationManager;

    @GetMapping
    public ApiResponse<List<ReservationResponse>> getAllReservations(@AuthenticationPrincipal Long adminId) {
        List<ReservationResponse> reservations = adminReservationManager.getAllReservations(adminId);
        return ApiResponse.success(reservations);
    }
}