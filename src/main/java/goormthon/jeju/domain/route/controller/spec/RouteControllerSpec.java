package goormthon.jeju.domain.route.controller.spec;

import goormthon.jeju.domain.route.dto.RouteResponse;
import goormthon.jeju.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Route", description = "노선 관련 API")
public interface RouteControllerSpec {

    @Operation(
            summary = "노선 목록 조회",
            description = "제주대학교병원 노선 목록을 조회합니다. 정렬 옵션을 통해 다양한 방식으로 노선을 정렬할 수 있습니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "노선 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RouteResponse.class))
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<List<RouteResponse>> getRoutes(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(
                    description = "정렬 방식 (default: 기본정렬, time: 시간순, expectedTime: 예상시간순)",
                    example = "default"
            )
            @RequestParam(defaultValue = "default") String sortBy
    );

    @Operation(
            summary = "노선 상세 정보 조회",
            description = "특정 노선의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "노선 상세 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = RouteResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 노선",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<RouteResponse> getRouteDetail(
            @Parameter(
                    description = "노선 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long routeId
    );
}