package gift.controller.kakaoLoginController;

import gift.config.KakaoProperties;
import gift.service.kakaoService.KakaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final KakaoProperties kakaoProperties;

    public KakaoLoginController(KakaoService kakaoService, KakaoProperties kakaoProperties) {
        this.kakaoService = kakaoService;
        this.kakaoProperties = kakaoProperties;

    }


    @GetMapping("/login/page")
    public ResponseEntity<Void> redirectToKakao() {
        String location = "https://kauth.kakao.com/oauth/authorize" + "?response_type=code" + "&client_id=" + kakaoProperties.clientId() + "&redirect_uri=" + kakaoProperties.redirectUri();
        System.out.println(location);
        return ResponseEntity.status(302).header("Location", location).build();
    }

    @GetMapping
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);

        return new ResponseEntity<>(accessToken, HttpStatus.CREATED);
    }
}
