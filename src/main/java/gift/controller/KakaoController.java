package gift.controller;

import gift.dto.response.KakaoAuthTokenResponse;
import gift.kakao.KakaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoController {

    private final KakaoAuthService kakaoAuthService;

    public KakaoController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping(value = "/kakao-auth")
    public ResponseEntity<KakaoAuthTokenResponse> getAuthorizationToken(
        @RequestParam(value = "code") String authorizationCode
    ) {
        return kakaoAuthService.getAuthToken(authorizationCode);
    }
}
