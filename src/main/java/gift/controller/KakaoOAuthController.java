package gift.controller;

import gift.dto.KakaoTokenResponse;
import gift.service.KakaoOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @Autowired
    public KakaoOAuthController(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @GetMapping("/login/kakao")
    public void redirectToKakao(HttpServletResponse response,
                                @Value("${kakao.client-id}") String clientId,
                                @Value("${kakao.redirect-uri}") String redirectUri) throws IOException {
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";

        response.sendRedirect(url);
    }

    @GetMapping("/callback/kakao")
    public KakaoTokenResponse callback(@RequestParam("code") String code) {
        return kakaoOAuthService.getToken(code);
    }
}


