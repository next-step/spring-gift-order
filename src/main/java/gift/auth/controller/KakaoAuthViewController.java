package gift.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoAuthViewController {

    private static final String AUTHORIZE_URL = "https://kauth.kakao.com/oauth/authorize";

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @GetMapping("/login/kakao")
    public String loginPage() {
        return "login-kakao";
    }

    @GetMapping("/oauth/kakao")
    public String redirectToKakao() {
        String url = AUTHORIZE_URL
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=talk_message";
        return "redirect:" + url;
    }
}