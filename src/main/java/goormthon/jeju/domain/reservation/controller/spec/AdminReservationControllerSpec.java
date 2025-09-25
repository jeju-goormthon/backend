package goormthon.jeju.domain.reservation.controller.spec;

import goormthon.jeju.domain.reservation.dto.ReservationResponse;
import goormthon.jeju.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "Admin - Reservation", description = "관리자 예약 관리 API")
public interface AdminReservationControllerSpec {

    @Operation(
            summary = "모든 예약 조회 (관리자)",
            description = "관리자가 시스템의 모든 예약을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "전체 예약 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class))
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한이 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<List<ReservationResponse>> getAllReservations(
            @Parameter(hidden = true) @AuthenticationPrincipal Long adminId
    );
}