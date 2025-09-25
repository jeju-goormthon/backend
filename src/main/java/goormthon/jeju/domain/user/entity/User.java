package goormthon.jeju.domain.user.entity;

import goormthon.jeju.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String password;

    @Column(length = 100)
    private String email;

    @Column(length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MedicalDepartment medicalDepartment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Builder
    public User(String phoneNumber, String password, String email, String name, MedicalDepartment medicalDepartment, LoginType loginType, UserRole role) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
        this.name = name;
        this.medicalDepartment = medicalDepartment;
        this.loginType = loginType;
        this.role = role != null ? role : UserRole.USER;
    }

    public void updateMedicalDepartment(MedicalDepartment medicalDepartment) {
        this.medicalDepartment = medicalDepartment;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}