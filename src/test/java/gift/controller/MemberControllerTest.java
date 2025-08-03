package gift.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.LoginRequest;
import gift.dto.RegisterRequest;
import gift.repository.MemberRepository;
import gift.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 회원가입_성공() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("abc@naver.com", "1234");
        String json = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void 회원가입_실패_이메일_패턴_오류() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("abc", "1234");
        String json = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("유효한 이메일 형식이 아닙니다."));
    }

    @Test
    void 회원가입_실패_null값() throws Exception {
        // 이메일이 null
        RegisterRequest registerRequest = new RegisterRequest("", "1234");
        String json = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("이메일은 필수 입력 값입니다."));

        // 비밀번호가 null
        registerRequest = new RegisterRequest("abc@naver.com", "");
        json = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("비밀번호는 필수 입력 값입니다."));

        // 이메일과 비밀번호가 null
        registerRequest = new RegisterRequest("", "");
        json = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages", containsInAnyOrder(
                "이메일은 필수 입력 값입니다.",
                "비밀번호는 필수 입력 값입니다.")));
    }

    @Test
    void 회원가입_실패_중복된_email() throws Exception {
        memberService.save(new RegisterRequest("abc@naver.com", "1234"));

        RegisterRequest registerRequest = new RegisterRequest("abc@naver.com", "abcd");
        String json = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다: abc@naver.com"));
    }

    @Test
    void 로그인_성공() throws Exception {
        memberService.save(new RegisterRequest("abc@naver.com", "1234"));

        LoginRequest loginRequest = new LoginRequest("abc@naver.com", "1234");
        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void 로그인_실패_이메일_패턴_오류() throws Exception {
        LoginRequest loginRequest = new LoginRequest("abc", "1234");
        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("유효한 이메일 형식이 아닙니다."));
    }

    @Test
    void 로그인_실패_null값() throws Exception {
        // email이 null
        LoginRequest loginRequest = new LoginRequest("", "1234");
        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("이메일은 필수 입력 값입니다."));

        // password가 null
        loginRequest = new LoginRequest("abc@naver.com", "");
        json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("비밀번호는 필수 입력 값입니다."));

        // email과 password 모두 null
        loginRequest = new LoginRequest("", "");
        json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages", containsInAnyOrder(
                "이메일은 필수 입력 값입니다.",
                "비밀번호는 필수 입력 값입니다.")));
    }

    @Test
    void 로그인_실패_존재하지_않는_이메일() throws Exception {
        LoginRequest loginRequest = new LoginRequest("abc@naver.com", "1234");
        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다: abc@naver.com"));
    }

    @Test
    void 로그인_실패_패스워드_불일치() throws Exception {
        memberService.save(new RegisterRequest("abc@naver.com", "1234"));

        LoginRequest loginRequest = new LoginRequest("abc@naver.com", "abc");
        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }
}
