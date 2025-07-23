package gift.controller.login;

import gift.config.KakaoProperties;
import gift.dto.login.KakaoTokenDto;
import gift.service.member.OauthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginViewController {

    private final KakaoProperties kakaoProperties;
    private final OauthService oauthService;

    public LoginViewController(KakaoProperties kakaoProperties, OauthService oauthService) {
        this.kakaoProperties = kakaoProperties;
        this.oauthService = oauthService;
    }

    @GetMapping("/view/login")
    public String loginPage(Model model) {
        model.addAttribute("kakaoRestApiKey", kakaoProperties.restApiKey());
        model.addAttribute("redirectUri", kakaoProperties.redirectUri());

        return "login";
    }

    @GetMapping("/oauth/kakao")
    public String kakaoLogin(
        @RequestParam String code
    ) {
        String accessToken = oauthService.fetchKakaoToken(code);
        return "login-success";
    }
}
