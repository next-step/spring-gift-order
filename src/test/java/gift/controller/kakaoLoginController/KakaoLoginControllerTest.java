package gift.controller.kakaoLoginController;

import gift.Jwt.TokenUtils;
import gift.config.KakaoProperties;
import gift.service.kakaoService.KakaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KakaoLoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class KakaoLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KakaoService kakaoService;

    @MockBean
    private TokenUtils tokenUtils; // WebConfig를 위한 Mock

    @MockBean
    private KakaoProperties kakaoProperties; // KakaoLoginController 생성자 주입

    @BeforeEach
    void setUp() {
        Mockito.when(kakaoProperties.clientId()).thenReturn("test-client-id");
        Mockito.when(kakaoProperties.redirectUri()).thenReturn("http://localhost:8080/callback");
    }

    @Test
    @DisplayName("카카오 인가 요청 리디렉션 테스트")
    void redirectToKakao_shouldReturn302() throws Exception {
        mockMvc.perform(get("/login/page"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString("https://kauth.kakao.com/oauth/authorize")));
    }

    @Test
    @DisplayName("인가 코드로 토큰 요청 성공 시 201 반환")
    void callback_withCode_shouldReturnAccessToken() throws Exception {
        Mockito.when(kakaoService.getAccessTokenFromKakao(anyString()))
                .thenReturn("mock-access-token");

        mockMvc.perform(get("/").param("code", "test-code"))
                .andExpect(status().isCreated())
                .andExpect(content().string("mock-access-token"));
    }
}
