package gift.controller;

import gift.config.KakaoOauthProperties;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/oauth")
public class OAuthController {

    private final KakaoOauthProperties kakaoOauthProperties;

    public OAuthController(KakaoOauthProperties kakaoOauthProperties) {
        this.kakaoOauthProperties = kakaoOauthProperties;
    }

    @GetMapping("/kakao")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        String url = "https://kauth.kakao.com/oauth/authorize?"
                + "client_id=" + kakaoOauthProperties.getClientId()
                + "&redirect_uri=" + kakaoOauthProperties.getRedirectUri()
                + "&response_type=code"
                + "&scope=talk_message";

        response.sendRedirect(url);
    }
}
