package goormthon.jeju.domain.inquiry.controller.spec;

import goormthon.jeju.domain.inquiry.dto.AnswerInquiryRequest;
import goormthon.jeju.domain.inquiry.dto.CreateInquiryRequest;
import goormthon.jeju.domain.inquiry.dto.InquiryResponse;
import goormthon.jeju.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Inquiry", description = "문의 관련 API")
public interface InquiryControllerSpec {

    @Operation(
            summary = "문의 생성",
            description = "새로운 문의를 등록합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "문의 생성 성공",
                    content = @Content(schema = @Schema(implementation = InquiryResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<InquiryResponse> createInquiry(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateInquiryRequest request
    );

    @Operation(
            summary = "내 문의 목록 조회",
            description = "현재 로그인한 사용자의 모든 문의 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "문의 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = InquiryResponse.class))
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<List<InquiryResponse>> getMyInquiries(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    @Operation(
            summary = "문의 상세 정보 조회",
            description = "특정 문의의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "문의 상세 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = InquiryResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 문의",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "접근 권한이 없는 문의",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<InquiryResponse> getInquiryDetail(
            @Parameter(
                    description = "문의 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long inquiryId
    );

    @Operation(
            summary = "문의 답변 등록 (관리자)",
            description = "관리자가 문의에 대한 답변을 등록합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "답변 등록 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한이 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 문의",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<Void> answerInquiry(
            @Parameter(hidden = true) @AuthenticationPrincipal Long adminId,
            @Parameter(
                    description = "문의 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long inquiryId,
            @Valid @RequestBody AnswerInquiryRequest request
    );

    @Operation(
            summary = "모든 문의 조회 (관리자)",
            description = "관리자가 시스템의 모든 문의를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "전체 문의 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = InquiryResponse.class))
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한이 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<List<InquiryResponse>> getAllInquiries(
            @Parameter(hidden = true) @AuthenticationPrincipal Long adminId
    );
}