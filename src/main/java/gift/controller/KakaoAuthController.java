package gift.controller;

import gift.dto.KakaoTokenDto;
import gift.service.KakaoAuthService;
import org.springframework.http.HttpStatus;
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

    /**
     *
     * @param code 사용자 인가 코드
     * @return KakaoTokenDto 카카오가 발급한 토큰
     */
    @GetMapping
    public ResponseEntity<KakaoTokenDto> getKakaoToken(@RequestParam("code") String code) {
        return new ResponseEntity<>(kakaoAuthService.getKakaoToken(code), HttpStatus.OK);
    }
}
