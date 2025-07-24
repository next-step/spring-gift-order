package gift.authorization.oauth;

import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/kakao")
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    public KakaoOAuthController(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @GetMapping("/login")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        response.sendRedirect(kakaoOAuthService.getKakaoLoginUrl());
    }

    @GetMapping("/callback")
    public ResponseEntity<KakaoTokenResponseDto> kakaoCallback(@RequestParam String code) {
        KakaoTokenResponseDto token = kakaoOAuthService.requestAccessToken(code);
        return ResponseEntity.ok(token);
    }
}