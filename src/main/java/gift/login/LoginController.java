package gift.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Value("${kakao.app.key}")
    private String clientId;

    @Value("${kakao.redirect_url}")
    private String redirectUri;

    @GetMapping("/login")
    public String login(Model model) {
        String location =
            "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;
        model.addAttribute("location", location);

        return "login";
    }

}
