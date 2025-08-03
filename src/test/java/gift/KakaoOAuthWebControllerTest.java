package gift;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KakaoOAuthWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${oauth.kakao.rest-api-key}")
    private String restApiKey;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    @Test
    void 카카오_OAuth_메인페이지_표시() throws Exception {
        mockMvc.perform(get("/oauth/kakao"))
                .andExpect(status().isOk())
                .andExpect(view().name("oauth/kakao/main"));
    }

    @Test
    void 카카오_OAuth_로그인_리다이렉트() throws Exception {
        String expectedUrl = "https://kauth.kakao.com/oauth/authorize?scope=account_email,profile_nickname,talk_message&response_type=code&client_id="
                + restApiKey + "&redirect_uri=" + redirectUri;
        
        mockMvc.perform(get("/oauth/kakao/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedUrl));
    }
} 