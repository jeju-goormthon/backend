package goormthon.jeju.domain.route.controller;

import goormthon.jeju.domain.route.dto.RouteResponse;
import goormthon.jeju.domain.route.manager.RouteManager;
import goormthon.jeju.global.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RouteController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RouteManager routeManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("병원명 파라미터 없이 노선 목록을 조회할 수 있다")
    void shouldGetRoutesWithoutHospitalNameParameter() throws Exception {
        // Given
        List<RouteResponse> mockRoutes = Arrays.asList(
                new RouteResponse(1L, "제주대학교병원", "09:00", "09:30", 30, 15, 20, "애월읍사무소 앞"),
                new RouteResponse(2L, "제주대학교병원", "10:00", "10:30", 30, 12, 15, "애월읍사무소 앞")
        );

        when(routeManager.getRoutes(eq("default"))).thenReturn(mockRoutes);

        // When & Then
        mockMvc.perform(get("/api/routes/list")
                        .param("sortBy", "default")
                        .principal(() -> "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].hospitalName").value("제주대학교병원"))
                .andExpect(jsonPath("$.data[0].pickupLocation").value("애월읍사무소 앞"))
                .andExpect(jsonPath("$.data[0].expectedTime").value(30))
                .andExpect(jsonPath("$.data[1].hospitalName").value("제주대학교병원"))
                .andExpect(jsonPath("$.data[1].pickupLocation").value("애월읍사무소 앞"))
                .andExpect(jsonPath("$.data[1].expectedTime").value(30));
    }

    @Test
    @DisplayName("모든 노선의 병원명은 제주대학교병원이어야 한다")
    void shouldReturnAllRoutesWithSameHospitalName() throws Exception {
        // Given
        List<RouteResponse> mockRoutes = Arrays.asList(
                new RouteResponse(1L, "제주대학교병원", "09:00", "09:30", 30, 15, 20, "애월읍사무소 앞"),
                new RouteResponse(2L, "제주대학교병원", "10:00", "10:30", 30, 12, 15, "애월읍사무소 앞"),
                new RouteResponse(3L, "제주대학교병원", "11:00", "11:30", 30, 18, 25, "애월읍사무소 앞")
        );

        when(routeManager.getRoutes(eq("time"))).thenReturn(mockRoutes);

        // When & Then
        mockMvc.perform(get("/api/routes/list")
                        .param("sortBy", "time")
                        .principal(() -> "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].hospitalName").value("제주대학교병원"))
                .andExpect(jsonPath("$.data[1].hospitalName").value("제주대학교병원"))
                .andExpect(jsonPath("$.data[2].hospitalName").value("제주대학교병원"));
    }

    @Test
    @DisplayName("노선 목록에는 필수 필드들이 모두 포함되어야 한다")
    void shouldReturnRoutesWithAllRequiredFields() throws Exception {
        // Given
        RouteResponse mockRoute = new RouteResponse(1L, "제주대학교병원", "09:00", "09:30", 30, 15, 20, "애월읍사무소 앞");

        when(routeManager.getRoutes(eq("default"))).thenReturn(Arrays.asList(mockRoute));

        // When & Then
        mockMvc.perform(get("/api/routes/list")
                        .principal(() -> "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].hospitalName").exists())
                .andExpect(jsonPath("$.data[0].pickupLocation").exists())
                .andExpect(jsonPath("$.data[0].startAt").exists())
                .andExpect(jsonPath("$.data[0].endAt").exists())
                .andExpect(jsonPath("$.data[0].expectedTime").exists())
                .andExpect(jsonPath("$.data[0].totalSeat").exists())
                .andExpect(jsonPath("$.data[0].remainedSeat").exists());
    }
}