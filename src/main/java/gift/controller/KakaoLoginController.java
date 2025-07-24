package gift.controller;

import gift.config.KakaoProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class KakaoLoginController {

    private final KakaoProperties kakaoProperties;

    public KakaoLoginController(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }


    @GetMapping("/")
    public String loginPage(Model model) {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+kakaoProperties.getClientId()+"&redirect_uri="+kakaoProperties.getRedirectUri();
        model.addAttribute("location", location);

        return "login";
    }
}
