package gift.controller;

import gift.dto.response.KaKaoTokenResponseDto;
import gift.service.KakaoAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoAuthController {
    private final KakaoAuthService kaKaoAuthService;

    public KakaoAuthController(KakaoAuthService kaKaoAuthService) {
        this.kaKaoAuthService = kaKaoAuthService;
    }

    @GetMapping("/kakao")
    public KaKaoTokenResponseDto kakaoToken(@RequestParam("code") String code) {
        return kaKaoAuthService.requestToken(code);
    }
}
