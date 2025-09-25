package goormthon.jeju.domain.route.service;

import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MockRouteDataService {

    private static final String HOSPITAL_NAME = "제주대학교병원";
    private static final String PICKUP_LOCATION = "애월읍사무소 앞";

    public List<Route> getMockRoutesByDepartment(MedicalDepartment department, String sortBy) {
        List<Route> allRoutes = generateMockRoutes();

        // 진료과목별로 필터링
        List<Route> filteredRoutes = allRoutes.stream()
                .filter(route -> route.getMedicalDepartment() == department)
                .collect(Collectors.toList());

        // 정렬 적용
        return sortRoutes(filteredRoutes, sortBy);
    }

    private List<Route> generateMockRoutes() {
        List<Route> routes = new ArrayList<>();

        // 내과 노선들
        routes.add(createRoute(1L, MedicalDepartment.INTERNAL_MEDICINE, LocalTime.of(9, 0), LocalTime.of(9, 30), 20, 5));
        routes.add(createRoute(2L, MedicalDepartment.INTERNAL_MEDICINE, LocalTime.of(10, 0), LocalTime.of(10, 30), 15, 3));
        routes.add(createRoute(3L, MedicalDepartment.INTERNAL_MEDICINE, LocalTime.of(14, 0), LocalTime.of(14, 30), 25, 7));

        // 피부과 노선들
        routes.add(createRoute(4L, MedicalDepartment.DERMATOLOGY, LocalTime.of(9, 30), LocalTime.of(10, 0), 18, 4));
        routes.add(createRoute(5L, MedicalDepartment.DERMATOLOGY, LocalTime.of(11, 0), LocalTime.of(11, 30), 22, 8));
        routes.add(createRoute(6L, MedicalDepartment.DERMATOLOGY, LocalTime.of(15, 0), LocalTime.of(15, 30), 20, 6));

        // 정형외과 노선들
        routes.add(createRoute(7L, MedicalDepartment.ORTHOPEDICS, LocalTime.of(8, 30), LocalTime.of(9, 0), 15, 2));
        routes.add(createRoute(8L, MedicalDepartment.ORTHOPEDICS, LocalTime.of(13, 0), LocalTime.of(13, 30), 20, 5));
        routes.add(createRoute(9L, MedicalDepartment.ORTHOPEDICS, LocalTime.of(16, 0), LocalTime.of(16, 30), 25, 9));

        // 신경과 노선들
        routes.add(createRoute(10L, MedicalDepartment.NEUROLOGY, LocalTime.of(10, 30), LocalTime.of(11, 0), 12, 3));
        routes.add(createRoute(11L, MedicalDepartment.NEUROLOGY, LocalTime.of(14, 30), LocalTime.of(15, 0), 18, 6));

        // 안과 노선들
        routes.add(createRoute(12L, MedicalDepartment.OPHTHALMOLOGY, LocalTime.of(9, 0), LocalTime.of(9, 30), 16, 4));
        routes.add(createRoute(13L, MedicalDepartment.OPHTHALMOLOGY, LocalTime.of(11, 30), LocalTime.of(12, 0), 20, 7));

        // 이비인후과 노선들
        routes.add(createRoute(14L, MedicalDepartment.ENT, LocalTime.of(8, 0), LocalTime.of(8, 30), 14, 2));
        routes.add(createRoute(15L, MedicalDepartment.ENT, LocalTime.of(15, 30), LocalTime.of(16, 0), 18, 5));

        // 외과 노선들
        routes.add(createRoute(16L, MedicalDepartment.GENERAL_SURGERY, LocalTime.of(7, 30), LocalTime.of(8, 0), 12, 1));
        routes.add(createRoute(17L, MedicalDepartment.GENERAL_SURGERY, LocalTime.of(12, 30), LocalTime.of(13, 0), 16, 4));

        // 비뇨의학과 노선들
        routes.add(createRoute(18L, MedicalDepartment.UROLOGY, LocalTime.of(10, 0), LocalTime.of(10, 30), 15, 3));
        routes.add(createRoute(19L, MedicalDepartment.UROLOGY, LocalTime.of(16, 30), LocalTime.of(17, 0), 20, 6));

        // 정신건강의학과 노선들
        routes.add(createRoute(20L, MedicalDepartment.PSYCHIATRY, LocalTime.of(13, 30), LocalTime.of(14, 0), 18, 5));
        routes.add(createRoute(21L, MedicalDepartment.PSYCHIATRY, LocalTime.of(17, 0), LocalTime.of(17, 30), 22, 8));

        // 재활의학과 노선들
        routes.add(createRoute(22L, MedicalDepartment.REHABILITATION, LocalTime.of(9, 30), LocalTime.of(10, 0), 14, 2));
        routes.add(createRoute(23L, MedicalDepartment.REHABILITATION, LocalTime.of(14, 0), LocalTime.of(14, 30), 16, 4));

        return routes;
    }

    private Route createRoute(Long id, MedicalDepartment department, LocalTime startTime, LocalTime endTime, int totalSeats, int bookedSeats) {
        int expectedMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();

        return Route.builder()
                .hospitalName(HOSPITAL_NAME)
                .medicalDepartment(department)
                .startTime(startTime)
                .endTime(endTime)
                .expectedMinutes(expectedMinutes)
                .totalSeats(totalSeats)
                .bookedSeats(bookedSeats)
                .pickupLocation(PICKUP_LOCATION)
                .build();
    }

    private List<Route> sortRoutes(List<Route> routes, String sortBy) {
        return switch (sortBy) {
            case "startTime", "time" -> routes.stream()
                    .sorted((r1, r2) -> r1.getStartTime().compareTo(r2.getStartTime()))
                    .collect(Collectors.toList());
            case "endTime" -> routes.stream()
                    .sorted((r1, r2) -> r1.getEndTime().compareTo(r2.getEndTime()))
                    .collect(Collectors.toList());
            case "expectedTime" -> routes.stream()
                    .sorted((r1, r2) -> r1.getExpectedMinutes().compareTo(r2.getExpectedMinutes()))
                    .collect(Collectors.toList());
            default -> routes; // 기본 정렬 (ID 순서)
        };
    }
}