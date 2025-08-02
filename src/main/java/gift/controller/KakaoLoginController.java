package gift.controller;

import gift.service.KakaoLoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    public KakaoLoginController(KakaoLoginService kakaoLoginService) {
        this.kakaoLoginService = kakaoLoginService;
    }

    @GetMapping("kakao/form")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("kakao/login")
    public String redirectToKakaoAuth(@RequestParam String clientId, HttpSession session) {

        session.setAttribute("clientId", clientId);
        session.setAttribute("redirectUri", "http://3.104.116.47:8080");

        String kakaoAuthUrl = UriComponentsBuilder
                .fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", "http://3.104.116.47:8080")
                .queryParam("scope", "talk_message")
                .build()
                .toUriString();

        return "redirect:" + kakaoAuthUrl;
    }

    @GetMapping
    public String redirectToKakaoLogin(@RequestParam String code, HttpSession session) {
        try {
            String jwt = kakaoLoginService.loginAndIssueJwt(code, session);
            session.setAttribute("jwtAccessToken", jwt);
            return "redirect:/message";
        } catch (Exception e) {
            e.printStackTrace();
            return "login";
        }
    }


}
