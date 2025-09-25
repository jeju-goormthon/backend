package goormthon.jeju.domain.inquiry.controller;

import goormthon.jeju.domain.inquiry.controller.spec.InquiryControllerSpec;
import goormthon.jeju.domain.inquiry.dto.AnswerInquiryRequest;
import goormthon.jeju.domain.inquiry.dto.CreateInquiryRequest;
import goormthon.jeju.domain.inquiry.dto.InquiryResponse;
import goormthon.jeju.domain.inquiry.manager.InquiryManager;
import goormthon.jeju.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController implements InquiryControllerSpec {

    private final InquiryManager inquiryManager;

    @PostMapping
    public ApiResponse<InquiryResponse> createInquiry(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateInquiryRequest request
    ) {
        InquiryResponse response = inquiryManager.createInquiry(
                userId,
                request.getTitle(),
                request.getContent()
        );
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<InquiryResponse>> getMyInquiries(@AuthenticationPrincipal Long userId) {
        List<InquiryResponse> inquiries = inquiryManager.getMyInquiries(userId);
        return ApiResponse.success(inquiries);
    }

    @GetMapping("/{inquiryId}")
    public ApiResponse<InquiryResponse> getInquiryDetail(@PathVariable Long inquiryId) {
        InquiryResponse inquiry = inquiryManager.getInquiryDetail(inquiryId);
        return ApiResponse.success(inquiry);
    }

    @PostMapping("/{inquiryId}/answer")
    public ApiResponse<Void> answerInquiry(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long inquiryId,
            @Valid @RequestBody AnswerInquiryRequest request
    ) {
        inquiryManager.answerInquiry(adminId, inquiryId, request.getAnswer());
        return ApiResponse.success();
    }

    @GetMapping("/admin/all")
    public ApiResponse<List<InquiryResponse>> getAllInquiries(@AuthenticationPrincipal Long adminId) {
        List<InquiryResponse> inquiries = inquiryManager.getAllInquiries(adminId);
        return ApiResponse.success(inquiries);
    }
}