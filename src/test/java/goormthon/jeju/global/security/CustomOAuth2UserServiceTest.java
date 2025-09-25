package goormthon.jeju.global.security;

import goormthon.jeju.domain.user.entity.LoginType;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.entity.UserRole;
import goormthon.jeju.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomOAuth2UserService 테스트")
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Test
    @DisplayName("신규 카카오 사용자 생성 - 성공")
    void createUser_KakaoUser_Success() {
        // given
        String email = "test@example.com";
        String name = "테스트사용자";
        String provider = "kakao";

        User expectedUser = User.builder()
                .email(email)
                .name(name)
                .loginType(LoginType.KAKAO)
                .role(UserRole.USER)
                .build();

        given(userRepository.save(any(User.class))).willReturn(expectedUser);

        // when
        User result = customOAuth2UserService.createUser(email, name, provider);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getLoginType()).isEqualTo(LoginType.KAKAO);
        assertThat(result.getRole()).isEqualTo(UserRole.USER);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Repository 저장 로직 검증")
    void createUser_RepositorySaveCall_Verification() {
        // given
        String email = "test@kakao.com";
        String name = "카카오유저";
        String provider = "kakao";

        User mockUser = User.builder()
                .email(email)
                .name(name)
                .loginType(LoginType.KAKAO)
                .role(UserRole.USER)
                .build();

        given(userRepository.save(any(User.class))).willReturn(mockUser);

        // when
        customOAuth2UserService.createUser(email, name, provider);

        // then
        verify(userRepository).save(any(User.class));
    }
}