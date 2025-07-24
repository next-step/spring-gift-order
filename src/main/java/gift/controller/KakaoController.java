package gift.controller;

import gift.kakao.KakaoAuthService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoController {

    private KakaoAuthService kakaoAuthService;

    private String authKey;

    public KakaoController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping(value = "/kakao-login")
    public ResponseEntity<String> getAuthorizationToken() {
        return kakaoAuthService.getAuthorization(authKey);
    }

    @GetMapping("/")
    public void handleAuthKey(@RequestParam Map<String, String> params) {
        authKey = params.get("code");
    }
}
