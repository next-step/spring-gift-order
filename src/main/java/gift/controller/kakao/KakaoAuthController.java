package gift.controller.kakao;

import gift.service.KakaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;

    public KakaoAuthController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> getAccessToken(@RequestParam("code") String authorizeCode) {
        String accessToken = kakaoAuthService.getAccessToken(authorizeCode);
        return ResponseEntity.ok(accessToken);
    }
}
