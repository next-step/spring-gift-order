package gift.controller.kakaoLoginController;

import gift.service.kakaoService.KakaoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final String clientId;
    private final String redirectUri;

    public KakaoLoginController(KakaoService kakaoService, @Value("${kakao.client_id}") String clientId, @Value("${kakao.redirect_uri}") String redirectUri) {
        this.kakaoService = kakaoService;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
    }

    @GetMapping("/url")
    public ResponseEntity<String> getKakaoLoginUrl() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" + "?response_type=code" + "&client_id=" + clientId + "&redirect_uri=" + redirectUri;

        return ResponseEntity.ok(kakaoAuthUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> callback(@RequestParam("code") String code) {
        String token = kakaoService.getAccessTokenFromKakao(code);

        Map<String, String> response = new HashMap<>();
        response.put("access_token", token);

        return ResponseEntity.ok(response);
    }
}
