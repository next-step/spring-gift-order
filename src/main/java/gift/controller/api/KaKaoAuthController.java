package gift.controller.api;

import gift.auth.cookie.CookieUtil;
import gift.dto.auth.AuthUser;
import gift.dto.auth.TokenResponse;
import gift.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/kakao")
public class KaKaoAuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    @Value("${base-url}")
    private String baseUrl;

    public KaKaoAuthController(AuthService authService, CookieUtil cookieUtil) {
        this.authService = authService;
        this.cookieUtil = cookieUtil;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String redirectUrl = authService.getRedirectUrl();
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(redirectUrl))
            .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(@RequestParam String code, HttpServletResponse response) {
        AuthUser authUser = authService.authenticate(code);
        TokenResponse tokenResponse = authService.registerOrLogin(authUser);

        cookieUtil.addAuthCookies(tokenResponse.token(), response);

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(baseUrl))
            .build();
    }
}
