package goormthon.jeju.domain.payment.controller.spec;

import goormthon.jeju.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Tag(name = "Payment Webhook", description = "결제 웹훅 API (외부 시스템 연동)")
public interface PaymentWebhookControllerSpec {

    @Operation(
            summary = "Toss 결제 웹훅",
            description = "Toss 결제 시스템에서 호출하는 웹훅 엔드포인트입니다. 결제 완료/취소 시 자동으로 호출됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "웹훅 처리 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 웹훅 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<Void> tossWebhook(
            @Parameter(
                    description = "Toss 웹훅 페이로드",
                    required = true,
                    schema = @Schema(
                            type = "object",
                            example = """
                                    {
                                        "paymentKey": "tgen_20240101_123456789",
                                        "orderId": "order_20240101_001",
                                        "status": "DONE",
                                        "amount": 50000
                                    }
                                    """
                    )
            )
            @RequestBody Map<String, Object> payload
    );

    @Operation(
            summary = "카카오페이 결제 웹훅",
            description = "카카오페이 결제 시스템에서 호출하는 웹훅 엔드포인트입니다. 결제 완료/취소/실패 시 자동으로 호출됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "웹훅 처리 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 웹훅 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ApiResponse<Void> kakaoWebhook(
            @Parameter(
                    description = "카카오페이 웹훅 페이로드",
                    required = true,
                    schema = @Schema(
                            type = "object",
                            example = """
                                    {
                                        "tid": "T12345678901234567890",
                                        "partner_order_id": "order_20240101_001",
                                        "status": "SUCCESS",
                                        "amount": {
                                            "total": 50000
                                        }
                                    }
                                    """
                    )
            )
            @RequestBody Map<String, Object> payload
    );
}