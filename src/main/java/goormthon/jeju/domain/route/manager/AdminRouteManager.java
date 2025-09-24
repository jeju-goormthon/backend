package goormthon.jeju.domain.route.manager;

import goormthon.jeju.domain.route.dto.CreateRouteRequest;
import goormthon.jeju.domain.route.dto.RouteResponse;
import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.route.service.RouteService;
import goormthon.jeju.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRouteManager {

    private final RouteService routeService;
    private final UserService userService;

    @Transactional
    public RouteResponse createRoute(Long adminId, CreateRouteRequest request) {
        userService.checkAdmin(adminId);

        Route route = Route.builder()
                .hospitalName(request.getHospitalName())
                .medicalDepartment(request.getMedicalDepartment())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .expectedMinutes(request.getExpectedMinutes())
                .totalSeats(request.getTotalSeats())
                .bookedSeats(0)
                .pickupLocation(request.getPickupLocation())
                .build();

        Route createdRoute = routeService.createRoute(route);
        return RouteResponse.from(createdRoute);
    }

    @Transactional
    public void updateRoute(Long adminId, Long routeId, CreateRouteRequest request) {
        userService.checkAdmin(adminId);

        Route updatedRoute = Route.builder()
                .hospitalName(request.getHospitalName())
                .medicalDepartment(request.getMedicalDepartment())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .expectedMinutes(request.getExpectedMinutes())
                .totalSeats(request.getTotalSeats())
                .pickupLocation(request.getPickupLocation())
                .build();

        routeService.updateRoute(routeId, updatedRoute);
    }

    @Transactional
    public void deleteRoute(Long adminId, Long routeId) {
        userService.checkAdmin(adminId);
        routeService.deleteRoute(routeId);
    }

    public List<RouteResponse> getAllRoutes(Long adminId) {
        userService.checkAdmin(adminId);
        List<Route> routes = routeService.getAllRoutes();
        return routes.stream()
                .map(RouteResponse::from)
                .toList();
    }
}