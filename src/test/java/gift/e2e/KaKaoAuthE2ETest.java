package gift.e2e;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.dto.auth.AuthUser;
import gift.dto.auth.TokenResponse;
import gift.service.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class KaKaoAuthE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("로그인 요청시 리다이렉트 URL 반환 성공")
    void test1() throws Exception {
        String redirectUrl = "https://kauth.kakao.com/oauth/authorize?...";

        given(authService.getRedirectUrl()).willReturn(redirectUrl);

        mockMvc.perform(get("/api/auth/kakao/login"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", redirectUrl));
    }

    @Test
    @DisplayName("콜백 요청 시 쿠키에 액세스 토큰 설정 후 리다이렉트")
    void test2() throws Exception {
        String code = "testCode";

        AuthUser mockUser = new AuthUser(
            123456L,
            "nickname",
            "email@test.com",
            "https://img.url",
            "mock-access-token",
            "mock-refresh-token"
        );
        TokenResponse mockToken = new TokenResponse("mock-access-token");

        given(authService.authenticate(code)).willReturn(mockUser);
        given(authService.registerOrLogin(mockUser)).willReturn(mockToken);

        mockMvc.perform(get("/api/auth/kakao/callback")
                .param("code", code))
            .andExpect(status().isFound())
            .andExpect(header().string("Set-Cookie",
                org.hamcrest.Matchers.containsString("access_token=mock-access-token")))
            .andExpect(
                header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
            .andExpect(header().string("Location", "/home"));
    }
}
