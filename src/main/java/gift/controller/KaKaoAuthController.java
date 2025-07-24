package gift.controller;

import gift.dto.AuthUser;
import gift.dto.TokenResponse;
import gift.service.AuthService;
import java.net.URI;
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

    public KaKaoAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String redirectUrl = authService.getRedirectUrl();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
    }

    @GetMapping("/callback")
    public ResponseEntity<TokenResponse> callback(@RequestParam String code) {
        AuthUser authUser = authService.authenticate(code);
        TokenResponse response = authService.registerOrLogin(authUser);

        return ResponseEntity.ok(response);
    }
}
