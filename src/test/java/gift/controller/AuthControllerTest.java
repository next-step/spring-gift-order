package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.UserRequestDto;
import gift.dto.UserResponseDto;
import gift.dto.TokenResponseDto;
import gift.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMappingContext;
    @MockitoBean
    private org.springframework.data.auditing.AuditingHandler jpaAuditingHandler;

    @Test
    @DisplayName("회원가입 성공 – 201 CREATED, UserResponseDto 검증")
    void signUpUser() throws Exception {
        // given
        var req = new UserRequestDto("user@example.com", "password123");
        var createdAt = LocalDateTime.of(2025, 7, 10, 22, 0);
        var resp = new UserResponseDto(1L, "user@example.com", "password123", createdAt);

        when(authService.userSignUp(any(UserRequestDto.class))).thenReturn(resp);

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.password").value("password123"))
                .andExpect(jsonPath("$.createdDate").value("2025-07-10T22:00:00"));
    }

    @Test
    @DisplayName("로그인 성공 – 200 OK, TokenResponseDto 검증")
    void loginUser() throws Exception {
        // given
        var req = new UserRequestDto("user@example.com", "password123");
        var token = "eyJhbGciOiJIUzI1NiJ9...";
        var resp = new TokenResponseDto(token);

        when(authService.userLogin(any(UserRequestDto.class))).thenReturn(resp);

        // when & then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Token").value(token));
    }
}

