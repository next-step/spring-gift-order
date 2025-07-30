package gift.controller;

import gift.dto.KakaoLoginRequest;
import gift.dto.TokenResponse;
import gift.service.KakaoLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KakaoLoginService kakaoLoginService;

    public AuthController(KakaoLoginService kakaoLoginService) {
        this.kakaoLoginService = kakaoLoginService;
    }

    @PostMapping("/kakao/login")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        TokenResponse response = kakaoLoginService.getAccessToken(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
