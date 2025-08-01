package gift.controller;

import gift.dto.kakao.LoginResponse;
import gift.service.KakaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoController {
    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @GetMapping("/")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestParam String code) {
        LoginResponse loginResponse = kakaoService.processKakaoLogin(code);
        return ResponseEntity.ok(loginResponse);
    }
}