package gift.controller.auth;

import gift.config.KakaoProperties;
import gift.service.auth.KakaoAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class KakaoLoginController {

    private final KakaoAuthService kakaoAuthService;
    private final KakaoProperties kakaoProperties;

    public KakaoLoginController(KakaoAuthService kakaoAuthService,
        KakaoProperties kakaoProperties) {
        this.kakaoAuthService = kakaoAuthService;
        this.kakaoProperties = kakaoProperties;
    }

    @GetMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin() {
        var uri = UriComponentsBuilder.fromUriString("https://kauth.kakao.com")
            .path("/oauth/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", kakaoProperties.getClientId())
            .queryParam("redirect_uri", kakaoProperties.getRedirectUri())
            .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);

        // FOUND: 302
        return ResponseEntity.status(HttpStatus.FOUND)
            .headers(headers).build();
    }

    @GetMapping("/oauth/kakao")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        String accessToken = kakaoAuthService.getAccessToken(code);

        return ResponseEntity.status(HttpStatus.OK)
            .body(accessToken);
    }
}
