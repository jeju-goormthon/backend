package goormthon.jeju.domain.user.dto;

import goormthon.jeju.domain.user.entity.MedicalDepartment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotNull(message = "진료과목은 필수입니다.")
    private MedicalDepartment medicalDepartment;
}