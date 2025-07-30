package gift.kakao.controller;

import gift.common.security.AuthenticatedMember;
import gift.common.security.LoginMember;
import gift.kakao.service.KakaoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oauth/kakao")
public class KakaoLoginController {

    private final String clientId;
    private final String redirectUri;

    private final KakaoService kakaoService;

    public KakaoLoginController(
        @Value("${kakao.app.key}") String clientId,
        @Value("${kakao.redirect_uri}") String redirectUri, KakaoService kakaoService
    ) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.kakaoService = kakaoService;
    }

    @GetMapping("/login")
    public String kakaoLogin(Model model) {
        String location =
            "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;
        model.addAttribute("location", location);

        return "login";
    }

    @GetMapping("/callback")
    public String kakaoCallback(@RequestParam("code") String code,
        @LoginMember AuthenticatedMember member) {
        kakaoService.fetchAndSaveToken(code, member.id());
        return "login-success";
    }

}
