package gift.controller;

import gift.dto.jwt.JwtTokenResponse;
import gift.dto.kakao.KakaoLoginResponse;
import gift.dto.kakao.KakaoTokenResponse;
import gift.dto.kakao.KakaoUserIdResponse;
import gift.service.KakaoLoginService;
import gift.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoUserApiController {

    private final KakaoLoginService kakaoLoginService;
    private final UserService userService;

    public KakaoUserApiController(KakaoLoginService kakaoLoginService, UserService userService) {
        this.kakaoLoginService = kakaoLoginService;
        this.userService = userService;
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(@RequestParam String code) {
        KakaoTokenResponse token = kakaoLoginService.getAccessToken(code);
        KakaoUserIdResponse kakaoUserId = kakaoLoginService.getUserInfo(token.accessToken());
        JwtTokenResponse jwtTokenResponse = userService.kakaoLogin(kakaoUserId.id());
        return ResponseEntity.ok(KakaoLoginResponse.of(token, jwtTokenResponse));
    }
}
