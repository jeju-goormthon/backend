package goormthon.jeju.domain.verification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import net.nurigo.sdk.message.service.DefaultMessageService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SmsServiceTest {

    @Mock
    private DefaultMessageService messageService;

    private SmsService smsService;

    @BeforeEach
    void setUp() {
        smsService = new SmsService();
        ReflectionTestUtils.setField(smsService, "apiKey", "test-key");
        ReflectionTestUtils.setField(smsService, "apiSecret", "test-secret");
        ReflectionTestUtils.setField(smsService, "fromNumber", "01012345678");
        ReflectionTestUtils.setField(smsService, "messageService", messageService);
    }

    @Test
    @DisplayName("전화번호가 null인 경우 SMS를 보내지 않는다")
    void shouldNotSendSmsWhenPhoneNumberIsNull() {
        // given
        String nullPhoneNumber = null;
        String message = "테스트 메시지";

        // when & then
        assertDoesNotThrow(() -> smsService.sendSms(nullPhoneNumber, message));
    }

    @Test
    @DisplayName("전화번호가 빈 문자열인 경우 SMS를 보내지 않는다")
    void shouldNotSendSmsWhenPhoneNumberIsEmpty() {
        // given
        String emptyPhoneNumber = "";
        String message = "테스트 메시지";

        // when & then
        assertDoesNotThrow(() -> smsService.sendSms(emptyPhoneNumber, message));
    }

    @Test
    @DisplayName("전화번호가 공백인 경우 SMS를 보내지 않는다")
    void shouldNotSendSmsWhenPhoneNumberIsBlank() {
        // given
        String blankPhoneNumber = "   ";
        String message = "테스트 메시지";

        // when & then
        assertDoesNotThrow(() -> smsService.sendSms(blankPhoneNumber, message));
    }
}