package gift.controller;

import gift.properties.KakaoProperties;
import gift.service.KakaoLoginService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class KakaoLoginController {

    private final KakaoProperties kakaoProperties;
    private final KakaoLoginService kakaoLoginService;

    private static final String KAKAO_AUTH_BASE_URL = "https://kauth.kakao.com/oauth/authorize";
    private static final String RESPONSE_TYPE = "code";
    private static final String SCOPE = "profile_nickname account_email";

    public KakaoLoginController(KakaoProperties kakaoProperties, KakaoLoginService kakaoLoginService) {
        this.kakaoProperties = kakaoProperties;
        this.kakaoLoginService = kakaoLoginService;
    }

    // 1. 카카오 로그인 버튼 클릭 시 → 인가 코드 요청
    @GetMapping("/oauth/kakao/login")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String url = UriComponentsBuilder
                .fromUriString(KAKAO_AUTH_BASE_URL)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("client_id", kakaoProperties.getClientId())
                .queryParam("redirect_uri", kakaoProperties.getRedirectUri())
                .queryParam("scope", "profile_nickname account_email") // 필수 동의 항목
                .queryParam("prompt", "consent") // 동의창 항상 뜨게(test 용도)
                .encode()
                .build()
                .toUriString();


        response.sendRedirect(url);
    }

    // 2. 카카오 로그인 완료 후 인가코드 콜백 처리
    @GetMapping("/oauth/kakao/callback")
    public String kakaoCallback(@RequestParam String code, HttpServletResponse response) {
        String kakaoAccessToken = kakaoLoginService.kakaoLogin(code, response);
        return "redirect:/user/products";
    }
}

