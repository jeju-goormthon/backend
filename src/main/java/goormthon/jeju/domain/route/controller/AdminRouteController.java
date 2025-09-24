package goormthon.jeju.domain.route.controller;

import goormthon.jeju.domain.route.dto.CreateRouteRequest;
import goormthon.jeju.domain.route.dto.RouteResponse;
import goormthon.jeju.domain.route.manager.AdminRouteManager;
import goormthon.jeju.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/routes")
@RequiredArgsConstructor
public class AdminRouteController {

    private final AdminRouteManager adminRouteManager;

    @PostMapping
    public ApiResponse<RouteResponse> createRoute(
            @AuthenticationPrincipal Long adminId,
            @Valid @RequestBody CreateRouteRequest request
    ) {
        RouteResponse response = adminRouteManager.createRoute(adminId, request);
        return ApiResponse.success(response);
    }

    @PutMapping("/{routeId}")
    public ApiResponse<Void> updateRoute(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long routeId,
            @Valid @RequestBody CreateRouteRequest request
    ) {
        adminRouteManager.updateRoute(adminId, routeId, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{routeId}")
    public ApiResponse<Void> deleteRoute(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long routeId
    ) {
        adminRouteManager.deleteRoute(adminId, routeId);
        return ApiResponse.success();
    }

    @GetMapping
    public ApiResponse<List<RouteResponse>> getAllRoutes(@AuthenticationPrincipal Long adminId) {
        List<RouteResponse> routes = adminRouteManager.getAllRoutes(adminId);
        return ApiResponse.success(routes);
    }
}