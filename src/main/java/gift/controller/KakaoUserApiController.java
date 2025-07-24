package gift.controller;

import gift.dto.kakao.KakaoTokenResponse;
import gift.service.KakaoTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoUserApiController {

    private final KakaoTokenProvider kakaoTokenProvider;

    public KakaoUserApiController(KakaoTokenProvider kakaoTokenProvider) {
        this.kakaoTokenProvider = kakaoTokenProvider;
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<KakaoTokenResponse> kakaoLogin(@RequestParam String code) {
        KakaoTokenResponse token = kakaoTokenProvider.getAccessToken(code);
        return ResponseEntity.ok(token);
    }
}
