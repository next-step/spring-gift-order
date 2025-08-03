package gift.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.dto.KakaoUserInfoResponseDto;
import gift.dto.TokenResponse;
import gift.service.KakaoService;
import gift.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class SocialLoginControllerTest {

    @Mock
    private KakaoService kakaoService;

    @Mock
    private MemberService memberService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SocialLoginController controller = new SocialLoginController(kakaoService, memberService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void 신규_회원일_경우_회원가입_URI로_리다이렉트() throws Exception {
        // given
        String code = "test-auth-code";
        String socialAccessToken = "test-access-token";
        Long socialId = 12345L;

        when(kakaoService.getAccessTokenFromKakao(code)).thenReturn(socialAccessToken);
        when(kakaoService.getUserInfo(socialAccessToken)).thenReturn(
            new KakaoUserInfoResponseDto(socialId));
        when(memberService.isSocialIdExists(socialId)).thenReturn(false);

        // when, then
        mockMvc.perform(get("/").param("code", code))
            .andExpect(status().isFound()) // 302 redirect
            .andExpect(redirectedUrl("/admin/members/new?socialId=" + socialId
                + "&providerType=kakao&socialAccessToken=" + socialAccessToken));
    }

    @Test
    void 기존_회원일_경우_토큰_응답() throws Exception {
        // given
        String code = "test-auth-code";
        String socialAccessToken = "test-access-token";
        Long socialId = 54321L;
        TokenResponse tokenResponse = new TokenResponse("access-token-value");

        when(kakaoService.getAccessTokenFromKakao(code)).thenReturn(socialAccessToken);
        when(kakaoService.getUserInfo(socialAccessToken)).thenReturn(
            new KakaoUserInfoResponseDto(socialId));
        when(memberService.isSocialIdExists(socialId)).thenReturn(true);
        when(memberService.findBySocialId(socialId)).thenReturn(tokenResponse);

        // when, then
        mockMvc.perform(get("/").param("code", code))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.token").value(tokenResponse.getToken()));
    }

}