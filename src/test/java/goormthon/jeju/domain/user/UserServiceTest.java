package goormthon.jeju.domain.user;

import goormthon.jeju.domain.user.entity.LoginType;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.repository.UserRepository;
import goormthon.jeju.domain.user.service.UserService;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("일반 회원가입 성공")
    void registerUser_Success() {
        String phoneNumber = "01012345678";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        MedicalDepartment department = MedicalDepartment.INTERNAL_MEDICINE;

        User user = User.builder()
                .phoneNumber(phoneNumber)
                .password(encodedPassword)
                .medicalDepartment(department)
                .loginType(LoginType.NORMAL)
                .build();

        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.registerNormalUser(phoneNumber, password, department);

        assertThat(savedUser.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(savedUser.getLoginType()).isEqualTo(LoginType.NORMAL);
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(password);
    }

    @Test
    @DisplayName("중복된 전화번호로 회원가입 실패")
    void registerUser_DuplicatePhoneNumber() {
        String phoneNumber = "01012345678";
        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

        assertThatThrownBy(() -> userService.registerNormalUser(phoneNumber, "password", MedicalDepartment.INTERNAL_MEDICINE))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_PHONE_NUMBER);
    }

    @Test
    @DisplayName("전화번호로 사용자 조회 성공")
    void findByPhoneNumber_Success() {
        String phoneNumber = "01012345678";
        User user = User.builder()
                .phoneNumber(phoneNumber)
                .password("password")
                .loginType(LoginType.NORMAL)
                .build();

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));

        User foundUser = userService.findByPhoneNumber(phoneNumber);

        assertThat(foundUser.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    @DisplayName("전화번호로 사용자 조회 실패")
    void findByPhoneNumber_NotFound() {
        String phoneNumber = "01012345678";
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByPhoneNumber(phoneNumber))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("진료과목 변경 성공")
    void updateMedicalDepartment_Success() {
        Long userId = 1L;
        MedicalDepartment newDepartment = MedicalDepartment.OPHTHALMOLOGY;

        User user = User.builder()
                .phoneNumber("01012345678")
                .password("password")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateMedicalDepartment(userId, newDepartment);

        assertThat(user.getMedicalDepartment()).isEqualTo(newDepartment);
    }
}