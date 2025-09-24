package goormthon.jeju.domain.route.repository;

import goormthon.jeju.domain.route.entity.Route;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByHospitalNameAndMedicalDepartment(String hospitalName, MedicalDepartment medicalDepartment);
    List<Route> findByHospitalNameAndMedicalDepartmentOrderByStartTimeAsc(String hospitalName, MedicalDepartment medicalDepartment);
    List<Route> findByHospitalNameAndMedicalDepartmentOrderByEndTimeAsc(String hospitalName, MedicalDepartment medicalDepartment);
    List<Route> findByHospitalNameAndMedicalDepartmentOrderByExpectedMinutesAsc(String hospitalName, MedicalDepartment medicalDepartment);
}