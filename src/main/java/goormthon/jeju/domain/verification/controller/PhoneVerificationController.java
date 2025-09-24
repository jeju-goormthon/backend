package goormthon.jeju.domain.verification.controller;

import goormthon.jeju.domain.verification.dto.SendVerificationRequest;
import goormthon.jeju.domain.verification.dto.VerifyCodeRequest;
import goormthon.jeju.domain.verification.service.PhoneVerificationService;
import goormthon.jeju.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class PhoneVerificationController {

    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/send")
    public ApiResponse<Void> sendVerificationCode(@Valid @RequestBody SendVerificationRequest request) {
        phoneVerificationService.sendVerificationCode(request.getPhoneNumber());
        return ApiResponse.success(null);
    }

    @PostMapping("/verify")
    public ApiResponse<Boolean> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        boolean isValid = phoneVerificationService.verifyCode(request.getPhoneNumber(), request.getCode());
        return ApiResponse.success(isValid);
    }
}