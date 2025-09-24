package goormthon.jeju.domain.user.dto;

import goormthon.jeju.domain.user.entity.LoginType;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String phoneNumber;
    private String email;
    private String name;
    private MedicalDepartment medicalDepartment;
    private LoginType loginType;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getName(),
                user.getMedicalDepartment(),
                user.getLoginType()
        );
    }
}