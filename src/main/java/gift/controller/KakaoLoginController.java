package gift.controller;


import gift.service.KakaoAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class KakaoLoginController {

    private final KakaoAuthService kakaoAuthService;

    public KakaoLoginController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }


    @GetMapping("/login")
    public String loginPage(Model model) {
        String location = kakaoAuthService.getAuthorizeUrl();
        model.addAttribute("location", location);

        return "login";
    }
}
