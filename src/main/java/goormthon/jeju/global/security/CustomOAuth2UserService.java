package goormthon.jeju.global.security;

import goormthon.jeju.domain.user.entity.LoginType;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.entity.UserRole;
import goormthon.jeju.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("OAuth2 로그인 - registrationId: {}", registrationId);
        log.info("OAuth2 사용자 정보: {}", attributes);

        String email;
        String name;

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }

        // 이메일이 없는 경우 예외 처리
        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("OAuth2 로그인에서 이메일 정보를 가져올 수 없습니다.");
        }

        // 사용자 찾기 또는 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, name, registrationId));

        log.info("OAuth2 사용자 처리 완료 - userId: {}, email: {}", user.getId(), user.getEmail());

        return oAuth2User;
    }

    User createUser(String email, String name, String provider) {
        log.info("새 OAuth2 사용자 생성 - email: {}, name: {}, provider: {}", email, name, provider);

        User user = User.builder()
                .email(email)
                .name(name)
                .loginType(LoginType.KAKAO)
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }
}