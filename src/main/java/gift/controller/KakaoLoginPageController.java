package gift.controller;

import gift.config.KakaoProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoLoginPageController {
    private final KakaoProperties kakaoProperties;

    public KakaoLoginPageController(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }

    @GetMapping("/kakao/login")
    public String loginPage(Model model) {
        model.addAttribute("location", kakaoProperties.getLoginUrl());

        return "kakao/login";
    }
}
