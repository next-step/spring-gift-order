package gift.controller;

import gift.dto.KakaoTokenDto;
import gift.dto.KakaoUserInfoDto;
import gift.service.KakaoAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return new ResponseEntity<> (kakaoAuthService.getKakaoToken(code), HttpStatus.OK);
    }

    @GetMapping("/kakao-info")
    public ResponseEntity<KakaoUserInfoDto> getKakaoUserInfo(@RequestBody KakaoTokenDto kakaoTokenDto) {
        return new ResponseEntity<>(kakaoAuthService.getKakaoUserInfo(kakaoTokenDto), HttpStatus.OK);
    }
}
