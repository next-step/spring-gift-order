package gift.controller;

import gift.service.KakaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/kakao")
public class KakaoController {

    private final KakaoAuthService kakaoAuthService;

    public KakaoController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam("code") String code) {
        String accessToken = kakaoAuthService.getAccessToken(code);
        return ResponseEntity.ok("카카오 인증 성공! Access Token: " + accessToken);
    }
}
