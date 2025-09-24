package goormthon.jeju.domain.user.controller;

import goormthon.jeju.domain.user.dto.*;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.manager.UserManager;
import goormthon.jeju.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserManager userManager;

    @PostMapping("/register")
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        TokenResponse response = userManager.register(
                request.getPhoneNumber(),
                request.getPassword(),
                request.getMedicalDepartment()
        );
        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = userManager.login(
                request.getPhoneNumber(),
                request.getPassword()
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