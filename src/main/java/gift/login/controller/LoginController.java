package gift.login.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final String clientId;
    private final String redirectUri;

    public LoginController(
        @Value("${kakao.app.key}") String clientId,
        @Value("${kakao.redirect_uri}") String redirectUri
    ) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
    }

    @GetMapping("/login")
    public String kakaoLogin(Model model) {
        String location =
            "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;
        model.addAttribute("location", location);

        return "login";
    }

}
