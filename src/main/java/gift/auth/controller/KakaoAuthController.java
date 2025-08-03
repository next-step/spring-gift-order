package gift.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import gift.auth.dto.AuthTokenResponseDto;
import gift.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoAuthController {

    private final AuthService authService;

    public KakaoAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping(value = "/", params = "code")
    public ResponseEntity<AuthTokenResponseDto> kakaoLogin(
            @RequestParam("code") String authorizationCode,
            HttpServletResponse response
    ) throws JsonProcessingException {
        AuthTokenResponseDto authToken = authService.loginWithKakao(authorizationCode);

        Cookie kakaoCookie = new Cookie("kakaoAccessToken", authToken.kakaoAccessToken());
        kakaoCookie.setHttpOnly(true);
        kakaoCookie.setSecure(false);
        kakaoCookie.setPath("/");
        kakaoCookie.setMaxAge(3600);

        response.addCookie(kakaoCookie);

        return ResponseEntity.ok(authToken);
    }
}
