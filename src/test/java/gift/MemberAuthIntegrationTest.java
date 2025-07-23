package gift;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MemberAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    String randomEmail;

    @BeforeEach
    void setUp() throws Exception {
        randomEmail = "user_" + UUID.randomUUID().toString() + "@email.com";
        Map<String, String> member = new HashMap<>();
        member.put("email", randomEmail);
        member.put("password", "password123");

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일로 로그인 시 403 Forbidden 반환")
    void 로그인_실패_존재하지_않는_이메일() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "notfound@email.com");
        loginRequest.put("password", "password123");

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호로 로그인 시 403 Forbidden 반환")
    void 로그인_실패_잘못된_비밀번호() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "testuser@email.com");
        loginRequest.put("password", "wrongpassword");

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("토큰 없음 - Authorization 헤더가 없을 경우 401 Unauthorized 반환")
    void 인증_실패_토큰_없음() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("토큰 유효하지 않음 - 잘못된 토큰으로 요청 시 401 Unauthorized 반환")
    void 인증_실패_유효하지_않은_토큰() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 시도 시 400 Bad Request 반환")
    void 회원가입_실패_중복_이메일() throws Exception {
        Map<String, String> duplicateMember = new HashMap<>();
        duplicateMember.put("email", randomEmail);
        duplicateMember.put("password", "anotherpassword");

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateMember)))
                .andExpect(status().isBadRequest());
    }
}
