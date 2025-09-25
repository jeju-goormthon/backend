package goormthon.jeju.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "U002", "이미 등록된 전화번호입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "U003", "비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "U004", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "U005", "만료된 토큰입니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.UNAUTHORIZED, "U006", "인증번호가 일치하지 않습니다."),

    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "노선을 찾을 수 없습니다."),
    NO_AVAILABLE_SEATS(HttpStatus.BAD_REQUEST, "R002", "예약 가능한 좌석이 없습니다."),

    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RS001", "예약을 찾을 수 없습니다."),
    ALREADY_BOARDED(HttpStatus.BAD_REQUEST, "RS002", "이미 탑승한 예약입니다."),
    CANNOT_CANCEL_AFTER_BOARDING(HttpStatus.BAD_REQUEST, "RS003", "탑승 후에는 예약을 취소할 수 없습니다."),
    RESERVATION_TIME_PASSED(HttpStatus.BAD_REQUEST, "RS004", "예약 시간이 지났습니다."),
    PAST_DATE_RESERVATION(HttpStatus.BAD_REQUEST, "RS005", "과거 날짜로 예약할 수 없습니다."),
    DUPLICATE_RESERVATION(HttpStatus.CONFLICT, "RS006", "이미 해당 노선에 예약이 존재합니다."),
    TIME_CONFLICT_RESERVATION(HttpStatus.CONFLICT, "RS007", "같은 시간대에 다른 예약이 존재합니다."),

    PASS_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "정기권을 찾을 수 없습니다."),
    PASS_ALREADY_EXISTS(HttpStatus.CONFLICT, "P002", "이미 유효한 정기권이 있습니다."),
    PASS_EXPIRED(HttpStatus.BAD_REQUEST, "P003", "정기권이 만료되었습니다."),

    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PM001", "결제에 실패했습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PM002", "결제 내역을 찾을 수 없습니다."),
    PAYMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "PM003", "결제가 완료되지 않았습니다."),

    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "문의를 찾을 수 없습니다."),

    ADMIN_ONLY(HttpStatus.FORBIDDEN, "A001", "관리자만 접근할 수 있습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}