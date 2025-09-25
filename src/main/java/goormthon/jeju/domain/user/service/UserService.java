package goormthon.jeju.domain.user.service;

import goormthon.jeju.domain.user.entity.LoginType;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.entity.UserRole;
import goormthon.jeju.domain.user.repository.UserRepository;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerNormalUser(String phoneNumber, String password, MedicalDepartment medicalDepartment) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new GlobalException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        User user = User.builder()
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(password))
                .medicalDepartment(medicalDepartment)
                .loginType(LoginType.NORMAL)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User registerKakaoUser(String email, String name, MedicalDepartment medicalDepartment) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = User.builder()
                            .email(email)
                            .name(name)
                            .phoneNumber("")
                            .medicalDepartment(medicalDepartment)
                            .loginType(LoginType.KAKAO)
                            .build();
                    return userRepository.save(user);
                });
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void updateMedicalDepartment(Long userId, MedicalDepartment medicalDepartment) {
        User user = findById(userId);
        user.updateMedicalDepartment(medicalDepartment);
    }

    public boolean validatePassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Transactional
    public void updatePhoneNumber(Long userId, String phoneNumber) {
        User user = findById(userId);
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new GlobalException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }
        user.updatePhoneNumber(phoneNumber);
    }

    public void checkAdmin(Long userId) {
        User user = findById(userId);
        if (user.getRole() != UserRole.ADMIN) {
            throw new GlobalException(ErrorCode.ADMIN_ONLY);
        }
    }

    @Transactional
    public User findOrCreateByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {
                    User user = User.builder()
                            .phoneNumber(phoneNumber)
                            .loginType(LoginType.NORMAL)
                            .build();
                    return userRepository.save(user);
                });
    }
}