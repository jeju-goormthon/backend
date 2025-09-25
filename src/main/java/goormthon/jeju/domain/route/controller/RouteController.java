package goormthon.jeju.domain.route.controller;

import goormthon.jeju.domain.route.controller.spec.RouteControllerSpec;
import goormthon.jeju.domain.route.dto.RouteResponse;
import goormthon.jeju.domain.route.manager.RouteManager;
import goormthon.jeju.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController implements RouteControllerSpec {

    private final RouteManager routeManager;

    @GetMapping("/list")
    public ApiResponse<List<RouteResponse>> getRoutes(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "default") String sortBy
    ) {
        List<RouteResponse> routes = routeManager.getRoutes(userId, sortBy);
        return ApiResponse.success(routes);
    }

    @GetMapping("/{routeId}")
    public ApiResponse<RouteResponse> getRouteDetail(@PathVariable Long routeId) {
        RouteResponse route = routeManager.getRouteDetail(routeId);
        return ApiResponse.success(route);
    }
}