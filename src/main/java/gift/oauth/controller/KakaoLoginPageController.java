package gift.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoLoginPageController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login/login";
    }
}
