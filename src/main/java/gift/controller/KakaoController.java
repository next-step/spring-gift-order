package gift.controller;

import gift.kakao.KakaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoController {

    private KakaoAuthService kakaoAuthService;

    public KakaoController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping(value = "/kakao-login")
    public ResponseEntity<String> getAuthorizationToken() {
        return kakaoAuthService.getAuthorization();
    }
}
