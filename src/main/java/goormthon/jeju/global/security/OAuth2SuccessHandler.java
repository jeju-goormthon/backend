package goormthon.jeju.global.security;

import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.repository.UserRepository;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("OAuth2 로그인 성공했으나 사용자를 찾을 수 없음: {}", email);
                    return new GlobalException(ErrorCode.USER_NOT_FOUND);
                });

        String targetUrl;

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            String tempToken = jwtTokenProvider.createAccessToken(user.getId());
            targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("tempToken", tempToken)
                    .queryParam("needPhoneVerification", true)
                    .build()
                    .toUriString();
        } else {
            String accessToken = jwtTokenProvider.createAccessToken(user.getId());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
            targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build()
                    .toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}