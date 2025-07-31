package gift.controller;

import gift.dto.KakaoLoginResponse;
import gift.dto.KakaoTokenResponse;
import gift.entity.vo.Email;
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
        String mockAccessToken = "access-token";
        String mockJwtToken = "jwt-token";

        KakaoTokenResponse mockResponse = new KakaoTokenResponse();
        mockResponse.setAccessToken(mockAccessToken);
        mockResponse.setRefreshToken("refresh-token");
        mockResponse.setTokenType("bearer");
        mockResponse.setExpiresIn(3600);

        Email mockEmail = new Email("test@test.com");

        when(kakaoApiService.getToken(code)).thenReturn(mockResponse);
        when(kakaoApiService.getEmail(mockAccessToken)).thenReturn(mockEmail);
        when(userService.kakaoRegister(mockEmail)).thenReturn(mockJwtToken);

        mockMvc.perform(get("/").param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").value(mockJwtToken))
                .andExpect(jsonPath("$.kakaoAccessToken").value(mockAccessToken));
    }
}