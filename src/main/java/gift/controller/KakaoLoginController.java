package gift.controller;

import gift.service.KakaoLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    public KakaoLoginController(KakaoLoginService kakaoLoginService) {
        this.kakaoLoginService = kakaoLoginService;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam String code) {

        String accessToken = kakaoLoginService.getAccessToken(code);

        return ResponseEntity.ok().build();
    }
}
