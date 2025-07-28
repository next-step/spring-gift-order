package gift.e2e;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.dto.AuthUser;
import gift.dto.TokenResponse;
import gift.service.AuthService;
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
    void loginSuccessReturnRedirectUrl() throws Exception {
        String redirectUrl = "https://kauth.kakao.com/oauth/authorize?...";

        given(authService.getRedirectUrl()).willReturn(redirectUrl);

        mockMvc.perform(get("/api/auth/kakao/login"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", redirectUrl));
    }

    @Test
    @DisplayName("콜백 요청시 토큰 반환 성공")
    void callbackSuccessReturnAccessToken() throws Exception {
        String code = "testCode";

        AuthUser mockUser = new AuthUser(123456L, "nickname", "email@test.com", "https://img.url");
        TokenResponse mockToken = new TokenResponse("mock-access-token");

        given(authService.authenticate(code)).willReturn(mockUser);
        given(authService.registerOrLogin(mockUser)).willReturn(mockToken);

        mockMvc.perform(get("/api/auth/kakao/callback")
                .param("code", code))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.token").value("mock-access-token"));
    }
}
