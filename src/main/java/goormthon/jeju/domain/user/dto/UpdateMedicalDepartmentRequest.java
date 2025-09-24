package goormthon.jeju.domain.user.dto;

import goormthon.jeju.domain.user.entity.MedicalDepartment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMedicalDepartmentRequest {

    @NotNull(message = "진료과목은 필수입니다.")
    private MedicalDepartment medicalDepartment;
}