package gift.controller;

import gift.service.KakaoOAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/oauth/kakao")
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    KakaoOAuthController(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        URI kakaoAuthUri = kakaoOAuthService.buildKakaoAuthUri();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(kakaoAuthUri)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam("code") String code) {
        String jwtToken = kakaoOAuthService.loginWithKakao(code);
        return ResponseEntity.ok(jwtToken);
    }
}