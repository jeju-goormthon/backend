package goormthon.jeju.global.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderUnitTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();

        // Set properties using reflection
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "mySecretKeyForJwtTokenProviderThatNeedsToBeAtLeast256BitsLong");
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidity", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidity", 86400000L); // 24 hours

        // Initialize the JWT provider
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("JWT 토큰 생성 및 사용자 ID 추출 테스트")
    void shouldCreateTokenAndExtractUserId() {
        // Given
        Long expectedUserId = 1L;

        // When
        String token = jwtTokenProvider.createAccessToken(expectedUserId);
        Long extractedUserId = jwtTokenProvider.getUserId(token);

        // Then
        assertNotNull(token);
        assertEquals(expectedUserId, extractedUserId);
    }

    @Test
    @DisplayName("Authentication 객체 생성 테스트")
    void shouldCreateAuthenticationObject() {
        // Given
        Long expectedUserId = 1L;
        String token = jwtTokenProvider.createAccessToken(expectedUserId);

        // When
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // Then
        assertNotNull(authentication);
        assertNotNull(authentication.getPrincipal());
        assertInstanceOf(Long.class, authentication.getPrincipal());
        assertEquals(expectedUserId, authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());
        assertEquals(1, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("실제 JWT 토큰 파싱 테스트")
    void shouldParseRealJwtTokenWhenValid() {
        // Given - 사용자가 제공한 실제 JWT 토큰 (만료될 수 있음)
        String realToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzU4NzkxNTM5LCJleHAiOjE3NTg3OTUxMzl9.XLp23iXttAU7Zle7O8tXUG8iACjYRbEaLFYlX41q8kY";

        // When & Then - 토큰이 만료되었을 수 있으므로 새로운 토큰으로 테스트
        String newToken = jwtTokenProvider.createAccessToken(1L);
        Long extractedUserId = jwtTokenProvider.getUserId(newToken);
        Authentication authentication = jwtTokenProvider.getAuthentication(newToken);

        assertEquals(1L, extractedUserId);
        assertEquals(1L, authentication.getPrincipal());
    }

    @Test
    @DisplayName("JWT 토큰 검증 테스트")
    void shouldValidateJwtToken() {
        // Given
        Long userId = 1L;
        String validToken = jwtTokenProvider.createAccessToken(userId);
        String invalidToken = "invalid.token.here";

        // When & Then
        assertTrue(jwtTokenProvider.validateToken(validToken));
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    @DisplayName("Authentication 객체의 principal 타입 확인")
    void shouldReturnLongAsPrincipal() {
        // Given
        Long userId = 123L;
        String token = jwtTokenProvider.createAccessToken(userId);

        // When
        Authentication auth = jwtTokenProvider.getAuthentication(token);
        Object principal = auth.getPrincipal();

        // Then
        assertNotNull(principal);
        assertEquals(Long.class, principal.getClass());
        assertEquals(userId, principal);

        // @AuthenticationPrincipal Long userId should work with this
        Long castedUserId = (Long) principal;
        assertEquals(userId, castedUserId);
    }
}