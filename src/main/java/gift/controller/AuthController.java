package gift.controller;

import gift.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login/page")
    public String loginPage(Model model) {
        String location = authService.getKakaoAuthorizationUrl();
        model.addAttribute("location", location);
        return "kakao-login";
    }

    @GetMapping
    public ResponseEntity<?> kakaoLogin(
            @RequestParam("code") String accessCode) {
        // 받은 토큰은 추후에 사용을 위해 남겨둠
        String accessToken = authService.processKakaoLogin(accessCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
