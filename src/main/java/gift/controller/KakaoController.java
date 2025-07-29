package gift.controller;

import gift.dto.KakaoLoginResponse;
import gift.dto.KakaoTokenResponse;
import gift.dto.TokenResponse;
import gift.entity.vo.Email;
import gift.service.KakaoApiService;
import gift.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class KakaoController {

    private final KakaoApiService kakaoApiService;
    private final UserService userService;

    KakaoController(KakaoApiService kakaoApiService, UserService userService) {
        this.kakaoApiService = kakaoApiService;
        this.userService = userService;
    }

    @GetMapping("/kakao/login")
    public RedirectView authorize() {
        return new RedirectView(kakaoApiService.getAuthUrl());
    }

    @GetMapping
    public ResponseEntity<KakaoLoginResponse> getToken(@RequestParam String code) {
        KakaoTokenResponse tokenResponse = kakaoApiService.getToken(code);
        String accessToken = tokenResponse.getAccessToken();

        Email email = kakaoApiService.getEmail(accessToken);
        String token = userService.kakaoRegister(email);
        return new ResponseEntity<>(new KakaoLoginResponse(token, accessToken), HttpStatus.OK);
    }
}
