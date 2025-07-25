package gift.controller;

import gift.dto.kakao.KakaoTokenResponse;
import gift.service.KakaoLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoUserApiController {

    private final KakaoLoginService kakaoLoginService;

    public KakaoUserApiController(KakaoLoginService kakaoLoginService) {
        this.kakaoLoginService = kakaoLoginService;
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<KakaoTokenResponse> kakaoLogin(@RequestParam String code) {
        KakaoTokenResponse token = kakaoLoginService.getAccessToken(code);
        return ResponseEntity.ok(token);
    }
}
