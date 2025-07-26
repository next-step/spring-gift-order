package gift.controller;

import gift.dto.KakaoTokenResponse;
import gift.service.KakaoApiService;
import gift.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KakaoController.class)
class KakaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;


    @MockitoBean
    private KakaoApiService kakaoApiService;

    @Test
    void testAuthorize() throws Exception {
        String mockAuthUrl = "https://kauth.kakao.com/oauth/authorize?client_id=abc";

        when(kakaoApiService.getAuthUrl()).thenReturn(mockAuthUrl);

        mockMvc.perform(get("/kakao/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(mockAuthUrl));
    }

    @Test
    void testGetToken() throws Exception {
        String code = "mockCode";

        KakaoTokenResponse mockResponse = new KakaoTokenResponse();
        mockResponse.setAccessToken("access-token");
        mockResponse.setRefreshToken("refresh-token");
        mockResponse.setTokenType("bearer");
        mockResponse.setExpiresIn(3600);

        when(kakaoApiService.getToken(code)).thenReturn(mockResponse);

        mockMvc.perform(get("/").param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-token"))
                .andExpect(jsonPath("$.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.token_type").value("bearer"))
                .andExpect(jsonPath("$.expires_in").value(3600));
    }
}