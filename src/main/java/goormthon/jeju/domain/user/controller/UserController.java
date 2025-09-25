package goormthon.jeju.domain.user.controller;

import goormthon.jeju.domain.user.controller.spec.UserControllerSpec;
import goormthon.jeju.domain.user.dto.*;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.manager.UserManager;
import goormthon.jeju.domain.verification.dto.SendVerificationRequest;
import goormthon.jeju.domain.verification.dto.VerifyCodeRequest;
import goormthon.jeju.domain.verification.service.PhoneVerificationService;
import goormthon.jeju.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController implements UserControllerSpec {

    private final UserManager userManager;
    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/login")
    public ApiResponse<Void> login(@Valid @RequestBody SendVerificationRequest request) {
        phoneVerificationService.sendVerificationCode(request.getPhoneNumber());
        return ApiResponse.success();
    }

    @PostMapping("/login/verify")
    public ApiResponse<TokenResponse> verifyLogin(@Valid @RequestBody VerifyCodeRequest request) {
        TokenResponse response = phoneVerificationService.verifyCodeAndLogin(
                request.getPhoneNumber(),
                request.getCode()
        );
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo(@AuthenticationPrincipal Long userId) {
        User user = userManager.getUserInfo(userId);
        return ApiResponse.success(UserResponse.from(user));
    }

    @PatchMapping("/medical-department")
    public ApiResponse<Void> updateMedicalDepartment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateMedicalDepartmentRequest request
    ) {
        userManager.updateMedicalDepartment(userId, request.getMedicalDepartment());
        return ApiResponse.success();
    }

    @PatchMapping("/phone-number")
    public ApiResponse<TokenResponse> updatePhoneNumber(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdatePhoneNumberRequest request
    ) {
        TokenResponse response = userManager.updatePhoneNumber(userId, request.getPhoneNumber());
        return ApiResponse.success(response);
    }
}