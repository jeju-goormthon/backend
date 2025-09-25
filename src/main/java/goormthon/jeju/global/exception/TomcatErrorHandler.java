package goormthon.jeju.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class TomcatErrorHandler implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        Object errorMsg = request.getAttribute("jakarta.servlet.error.message");
        Object exception = request.getAttribute("jakarta.servlet.error.exception");

        int statusCode = status != null ? (Integer) status : 500;
        String message = errorMsg != null ? (String) errorMsg : "Unknown error";

        // HTTP Method 관련 오류 특별 처리
        if (message != null && message.contains("Invalid character found in method name")) {
            log.warn("HTTP Method parsing error detected - likely HTTPS to HTTP or binary data: {}", message);
            statusCode = 400;
            message = "잘못된 요청 형식입니다. HTTP 프로토콜을 확인해주세요.";
        }

        // 로깅
        if (statusCode >= 500) {
            log.error("서버 오류 - Status: {}, Message: {}, Exception: {}", statusCode, message, exception);
        } else if (statusCode >= 400) {
            log.warn("클라이언트 오류 - Status: {}, Message: {}", statusCode, message);
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURI());

        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}