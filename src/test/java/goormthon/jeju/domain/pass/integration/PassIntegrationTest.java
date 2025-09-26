package goormthon.jeju.domain.pass.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import goormthon.jeju.domain.pass.entity.PassType;
import goormthon.jeju.domain.pass.repository.PassRepository;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.repository.UserRepository;
import goormthon.jeju.global.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 정기권 단순 생성 통합 테스트
 *
 * 검증 시나리오:
 * 1. 정기권 생성 (단순 CRUD)
 * 2. 중복 정기권 생성 실패
 * 3. 정기권 조회 및 취소
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class PassIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassRepository passRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .phoneNumber("01012345678")
                .password("password")
                .name("테스트유저")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .build();
        testUser = userRepository.save(testUser);
        accessToken = jwtTokenProvider.createAccessToken(testUser.getId());
    }

    @Test
    @DisplayName("정기권 생성 성공")
    void createPass_Success() throws Exception {
        // Given: 사용자가 1개월 정기권을 생성하려고 함
        PassType passType = PassType.ONE_MONTH;

        // 초기 상태 검증
        assertThat(passRepository.findByUserOrderByCreatedAtDesc(testUser)).isEmpty();

        // When: 정기권 생성 요청
        String createRequest = """
                {
                    "passType": "ONE_MONTH"
                }
                """;

        mockMvc.perform(post("/api/passes")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.passType").value("ONE_MONTH"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        // Then: 정기권이 정상적으로 생성되었는지 검증
        var passes = passRepository.findByUserOrderByCreatedAtDesc(testUser);
        assertThat(passes).hasSize(1);

        var createdPass = passes.get(0);
        assertThat(createdPass.getPassType()).isEqualTo(PassType.ONE_MONTH);
        assertThat(createdPass.getStatus()).isEqualTo(goormthon.jeju.domain.pass.entity.PassStatus.ACTIVE);
        assertThat(createdPass.isValid()).isTrue();
    }

    @Test
    @DisplayName("이미 활성 정기권이 있는 경우 생성 실패")
    void createPass_AlreadyHasActivePass_Fail() throws Exception {
        // Given: 이미 활성 정기권이 있는 사용자
        String createRequest = """
                {
                    "passType": "ONE_MONTH"
                }
                """;

        // 첫 번째 정기권 생성
        mockMvc.perform(post("/api/passes")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isOk());

        // When: 두 번째 정기권 생성 시도
        mockMvc.perform(post("/api/passes")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("P002")); // PASS_ALREADY_EXISTS
    }

    @Test
    @DisplayName("정기권 목록 조회 성공")
    void getMyPasses_Success() throws Exception {
        // Given: 정기권을 생성
        String createRequest = """
                {
                    "passType": "THREE_MONTH"
                }
                """;

        mockMvc.perform(post("/api/passes")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest));

        // When: 정기권 목록 조회
        mockMvc.perform(get("/api/passes")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].passType").value("THREE_MONTH"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));
    }

    @Test
    @DisplayName("활성 정기권 조회 성공")
    void getActivePass_Success() throws Exception {
        // Given: 정기권을 생성
        String createRequest = """
                {
                    "passType": "SIX_MONTH"
                }
                """;

        mockMvc.perform(post("/api/passes")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest));

        // When: 활성 정기권 조회
        mockMvc.perform(get("/api/passes/active")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.passType").value("SIX_MONTH"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("정기권 취소 성공")
    void cancelPass_Success() throws Exception {
        // Given: 정기권을 생성
        String createRequest = """
                {
                    "passType": "ONE_MONTH"
                }
                """;

        mockMvc.perform(post("/api/passes")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest));

        // 생성된 정기권 ID 조회
        var passes = passRepository.findByUserOrderByCreatedAtDesc(testUser);
        Long passId = passes.get(0).getId();

        // When: 정기권 취소
        mockMvc.perform(delete("/api/passes/" + passId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Then: 정기권이 취소되었는지 검증
        var cancelledPass = passRepository.findById(passId).orElseThrow();
        assertThat(cancelledPass.getStatus()).isEqualTo(goormthon.jeju.domain.pass.entity.PassStatus.CANCELLED);
    }
}