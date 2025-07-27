package gift.authorization.oauth;

import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @GetMapping("/login")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        response.sendRedirect(kakaoService.getKakaoLoginUrl());
    }

    @GetMapping("/callback")
    public ResponseEntity<KakaoTokenResponseDto> kakaoCallback(@RequestParam String code) {
        KakaoTokenResponseDto token = kakaoService.requestAccessToken(code);
        return ResponseEntity.ok(token);
    }
}