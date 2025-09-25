package goormthon.jeju.domain.reservation.controller;

import goormthon.jeju.domain.reservation.controller.spec.ReservationControllerSpec;
import goormthon.jeju.domain.reservation.dto.CreateReservationRequest;
import goormthon.jeju.domain.reservation.dto.ReservationResponse;
import goormthon.jeju.domain.reservation.manager.ReservationManager;
import goormthon.jeju.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationControllerSpec {

    private final ReservationManager reservationManager;

    @PostMapping
    public ApiResponse<ReservationResponse> createReservation(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        ReservationResponse response = reservationManager.createReservation(
                userId,
                request.getRouteId(),
                request.getReservationDate()
        );
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<ReservationResponse>> getMyReservations(@AuthenticationPrincipal Long userId) {
        List<ReservationResponse> reservations = reservationManager.getMyReservations(userId);
        return ApiResponse.success(reservations);
    }

    @GetMapping("/{reservationId}")
    public ApiResponse<ReservationResponse> getReservationDetail(@PathVariable Long reservationId) {
        ReservationResponse reservation = reservationManager.getReservationDetail(reservationId);
        return ApiResponse.success(reservation);
    }

    @DeleteMapping("/{reservationId}")
    public ApiResponse<Void> cancelReservation(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reservationId
    ) {
        reservationManager.cancelReservation(userId, reservationId);
        return ApiResponse.success();
    }

    @PostMapping("/board")
    public ApiResponse<Void> boardReservation(@RequestParam String qrCode) {
        reservationManager.boardReservation(qrCode);
        return ApiResponse.success();
    }
}