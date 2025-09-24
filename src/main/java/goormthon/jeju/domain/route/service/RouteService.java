package goormthon.jeju.domain.route.service;

import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.route.repository.RouteRepository;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteService {

    private final RouteRepository routeRepository;

    public List<Route> getRoutesByHospitalAndDepartment(String hospitalName, MedicalDepartment medicalDepartment, String sortBy) {
        return switch (sortBy) {
            case "startTime" -> routeRepository.findByHospitalNameAndMedicalDepartmentOrderByStartTimeAsc(hospitalName, medicalDepartment);
            case "endTime" -> routeRepository.findByHospitalNameAndMedicalDepartmentOrderByEndTimeAsc(hospitalName, medicalDepartment);
            case "expectedTime" -> routeRepository.findByHospitalNameAndMedicalDepartmentOrderByExpectedMinutesAsc(hospitalName, medicalDepartment);
            default -> routeRepository.findByHospitalNameAndMedicalDepartment(hospitalName, medicalDepartment);
        };
    }

    public Route findById(Long routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new GlobalException(ErrorCode.ROUTE_NOT_FOUND));
    }

    @Transactional
    public void incrementBookedSeats(Long routeId) {
        Route route = findById(routeId);
        if (!route.hasAvailableSeats()) {
            throw new GlobalException(ErrorCode.NO_AVAILABLE_SEATS);
        }
        route.incrementBookedSeats();
    }

    @Transactional
    public void decrementBookedSeats(Long routeId) {
        Route route = findById(routeId);
        route.decrementBookedSeats();
    }

    @Transactional
    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    @Transactional
    public void updateRoute(Long routeId, Route updatedRoute) {
        Route route = findById(routeId);
        routeRepository.save(updatedRoute);
    }

    @Transactional
    public void deleteRoute(Long routeId) {
        Route route = findById(routeId);
        routeRepository.delete(route);
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
}