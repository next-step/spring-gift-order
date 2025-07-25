package gift.controller;

import gift.properties.KakaoProperties;
import gift.service.KakaoLoginService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class KakaoLoginController {

    private final KakaoProperties kakaoProperties;
    private final KakaoLoginService kakaoLoginService;

    public KakaoLoginController(KakaoProperties kakaoProperties, KakaoLoginService kakaoLoginService) {
        this.kakaoProperties = kakaoProperties;
        this.kakaoLoginService = kakaoLoginService;
    }

    // 1. 카카오 로그인 버튼 클릭 시 → 인가 코드 요청
    @GetMapping("/oauth/kakao/login")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String redirectUri = URLEncoder.encode(kakaoProperties.getRedirectUri(), StandardCharsets.UTF_8);
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + kakaoProperties.getClientId()
                + "&redirect_uri=" + redirectUri
                + "&scope=talk_message";

        response.sendRedirect(url);
    }

    // 2. 카카오 로그인 완료 후 인가코드 콜백 처리
    @GetMapping("/oauth/kakao/callback")
    public String kakaoCallback(@RequestParam String code, HttpServletResponse response) {
        kakaoLoginService.kakaoLogin(code, response);
        return "redirect:/user/products";
    }
}

