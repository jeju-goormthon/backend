package goormthon.jeju.domain.user.manager;

import goormthon.jeju.domain.user.dto.TokenResponse;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.service.UserService;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import goormthon.jeju.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserManager {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse register(String phoneNumber, String password, MedicalDepartment medicalDepartment) {
        User user = userService.registerNormalUser(phoneNumber, password, medicalDepartment);
        return createTokenResponse(user);
    }

    public TokenResponse login(String phoneNumber, String password) {
        User user = userService.findByPhoneNumber(phoneNumber);

        if (!userService.validatePassword(user, password)) {
            throw new GlobalException(ErrorCode.INVALID_PASSWORD);
        }

        return createTokenResponse(user);
    }

    @Transactional
    public void updateMedicalDepartment(Long userId, MedicalDepartment medicalDepartment) {
        userService.updateMedicalDepartment(userId, medicalDepartment);
    }

    public User getUserInfo(Long userId) {
        return userService.findById(userId);
    }

    @Transactional
    public TokenResponse updatePhoneNumber(Long userId, String phoneNumber) {
        userService.updatePhoneNumber(userId, phoneNumber);
        User user = userService.findById(userId);
        return createTokenResponse(user);
    }

    private TokenResponse createTokenResponse(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        return new TokenResponse(accessToken, refreshToken);
    }
}