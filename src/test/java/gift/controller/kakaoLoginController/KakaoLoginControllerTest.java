package gift.controller.kakaoLoginController;

import gift.Jwt.TokenUtils;
import gift.service.kakaoService.KakaoService;
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
@AutoConfigureMockMvc(addFilters = false) // 보안 필터 끔
class KakaoLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KakaoService kakaoService;

    @MockBean
    private TokenUtils tokenUtils;

    @Test
    @DisplayName("카카오 인가 요청 리디렉션 테스트")
    void redirectToKakao_shouldReturn302() throws Exception {
        mockMvc.perform(get("/login/page"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("https://kauth.kakao.com/oauth/authorize")));
    }

    @Test
    @DisplayName("인가 코드로 토큰 요청 성공 시 200 반환")
    void callback_withCode_shouldReturnAccessToken() throws Exception {
        Mockito.when(kakaoService.getAccessTokenFromKakao(anyString()))
                .thenReturn("mock-access-token");

        mockMvc.perform(get("/callback").param("code", "test-code")).andExpect(status().isOk());
    }
}

