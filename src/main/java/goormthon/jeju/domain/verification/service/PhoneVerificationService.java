package goormthon.jeju.domain.verification.service;

import goormthon.jeju.domain.user.dto.TokenResponse;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.service.UserService;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import goormthon.jeju.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneVerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SmsService smsService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String VERIFICATION_PREFIX = "verification:";
    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 5;

    public void sendVerificationCode(String phoneNumber) {
        String verificationCode = generateVerificationCode();

        String key = VERIFICATION_PREFIX + phoneNumber;
        redisTemplate.opsForValue().set(key, verificationCode, EXPIRATION_MINUTES, TimeUnit.MINUTES);

        String message = String.format("[제주 셔틀] 인증번호는 [%s]입니다. 5분 이내에 입력해주세요.", verificationCode);
        smsService.sendSms(phoneNumber, message);

        log.info("Verification code sent to {}: {}", phoneNumber, verificationCode);
    }

    public boolean verifyCode(String phoneNumber, String code) {
        String key = VERIFICATION_PREFIX + phoneNumber;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }

        if (!storedCode.equals(code)) {
            return false;
        }

        redisTemplate.delete(key);
        return true;
    }

    public TokenResponse verifyCodeAndLogin(String phoneNumber, String code) {
        String key = VERIFICATION_PREFIX + phoneNumber;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }

        if (!storedCode.equals(code)) {
            throw new GlobalException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        redisTemplate.delete(key);

        // OAuth 패턴: 사용자 찾기/생성
        User user = userService.findOrCreateByPhoneNumber(phoneNumber);

        // JWT 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        log.info("SMS login successful for user: {}", user.getId());
        return new TokenResponse(accessToken, refreshToken);
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}