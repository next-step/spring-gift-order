package gift.controller;

import gift.annotation.UserValid;
import gift.dto.KakaoTokenDto;
import gift.dto.KakaoUserInfoDto;
import gift.dto.UserInfoDto;
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
     * 기존 회원정보에 카카오 토큰 연동하는 API
     * @param userInfoDto 기존 사용자 정보
     * @param code 카카오 사용자 인가 정보
     * @return KakaoTokenDto 카카오 토큰
     */
    @GetMapping
    public ResponseEntity<KakaoTokenDto> accessKakaoToken(@UserValid UserInfoDto userInfoDto, @RequestParam("code") String code) {
        return new ResponseEntity<> (kakaoAuthService.accessKakaoToken(userInfoDto, code), HttpStatus.OK);
    }

    @GetMapping("/kakao-info")
    public ResponseEntity<KakaoUserInfoDto> getKakaoUserInfo(@RequestBody KakaoTokenDto kakaoTokenDto) {
        return new ResponseEntity<>(kakaoAuthService.getKakaoUserInfo(kakaoTokenDto), HttpStatus.OK);
    }
}
