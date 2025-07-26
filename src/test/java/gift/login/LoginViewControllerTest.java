package gift.login;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.config.KakaoProperties;
import gift.controller.login.LoginViewController;
import gift.resolver.LoginMemberArgumentResolver;
import gift.service.member.OauthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = LoginViewController.class)
public class LoginViewControllerTest {
    @Autowired
    MockMvc mockmvc;

    @MockitoBean
    OauthService oauthService;

    @MockitoBean
    KakaoProperties kakaoProperties;

    @MockitoBean
    LoginMemberArgumentResolver loginMemberArgumentResolver;

    @Test
    @DisplayName("카카오 로그인 성공 - 302 redirect")
    void kakaoLogin_success() throws Exception {
        String code = "fake-kakao-code";
        String token = "fake-kakao-token";

        given(oauthService.fetchKakaoToken(code))
            .willReturn(token);

        mockmvc.perform(get("/oauth/kakao").param("code", code))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login-success"));
    }
}
