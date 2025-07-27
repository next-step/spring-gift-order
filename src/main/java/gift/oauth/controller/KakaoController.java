package gift.oauth.controller;

import gift.oauth.service.KakaoService;
import gift.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oauth/kakao")
public class KakaoController {

    private final KakaoService kakaoService;
    private final JwtUtil jwtUtil;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public KakaoController(KakaoService kakaoService, JwtUtil jwtUtil) {
        this.kakaoService = kakaoService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/login")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String location = "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=profile_nickname&response_type=code";

        response.sendRedirect(location);
    }

    @GetMapping("/callback")
    public String kakaoCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {
        String accessToken = kakaoService.login(code);

        jwtUtil.addJwtToCookie(accessToken, response);

        return "redirect:/members/products";
    }
}
