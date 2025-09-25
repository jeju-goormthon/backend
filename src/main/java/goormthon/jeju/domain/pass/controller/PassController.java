package goormthon.jeju.domain.pass.controller;

import goormthon.jeju.domain.pass.controller.spec.PassControllerSpec;
import goormthon.jeju.domain.pass.dto.PassResponse;
import goormthon.jeju.domain.pass.dto.PurchasePassRequest;
import goormthon.jeju.domain.pass.manager.PassManager;
import goormthon.jeju.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passes")
@RequiredArgsConstructor
public class PassController implements PassControllerSpec {

    private final PassManager passManager;

    @PostMapping("/purchase")
    public ApiResponse<PassResponse> purchasePass(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PurchasePassRequest request
    ) {
        PassResponse response = passManager.purchasePass(
                userId,
                request.getPassType(),
                request.getPaymentMethod()
        );
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<PassResponse>> getMyPasses(@AuthenticationPrincipal Long userId) {
        List<PassResponse> passes = passManager.getMyPasses(userId);
        return ApiResponse.success(passes);
    }

    @GetMapping("/active")
    public ApiResponse<PassResponse> getActivePass(@AuthenticationPrincipal Long userId) {
        PassResponse pass = passManager.getActivePass(userId);
        return ApiResponse.success(pass);
    }

    @GetMapping("/check")
    public ApiResponse<Boolean> checkActivePass(@AuthenticationPrincipal Long userId) {
        boolean hasActivePass = passManager.hasActivePass(userId);
        return ApiResponse.success(hasActivePass);
    }

    @DeleteMapping("/{passId}")
    public ApiResponse<Void> cancelPass(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long passId
    ) {
        passManager.cancelPass(userId, passId);
        return ApiResponse.success();
    }
}