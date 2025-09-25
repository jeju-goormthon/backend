package goormthon.jeju.domain.route.manager;

import goormthon.jeju.domain.route.dto.RouteResponse;
import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.route.service.RouteService;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteManager {

    private final RouteService routeService;
    private final UserService userService;

    public List<RouteResponse> getRoutes(Long userId, String sortBy) {
        User user = userService.findById(userId);
        MedicalDepartment department = user.getMedicalDepartment();

        List<Route> routes = routeService.getRoutesByDepartment(department, sortBy);
        return routes.stream()
                .map(RouteResponse::from)
                .toList();
    }

    public RouteResponse getRouteDetail(Long routeId) {
        Route route = routeService.findById(routeId);
        return RouteResponse.from(route);
    }
}