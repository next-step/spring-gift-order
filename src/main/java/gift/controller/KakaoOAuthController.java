package gift.controller;

import gift.domain.Member;
import gift.dto.JwtResponse;
import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserResponse;
import gift.dto.LoginMemberResponse;
import gift.resolver.LoginMember;
import gift.service.AuthService;
import gift.service.KakaoOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;
    private final AuthService authService;

    @Autowired
    public KakaoOAuthController(KakaoOAuthService kakaoOAuthService, AuthService authService) {
        this.kakaoOAuthService = kakaoOAuthService;
        this.authService = authService;
    }

    @GetMapping("/login/kakao")
    public void redirectToKakao(HttpServletResponse response,
                                @Value("${kakao.client-id}") String clientId,
                                @Value("${kakao.redirect-uri}") String redirectUri) throws IOException {
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";

        response.sendRedirect(url);
    }

    @GetMapping("/oauth/callback/kakao")
    public JwtResponse callback(@RequestParam("code") String code) {
        KakaoTokenResponse tokenResponse = kakaoOAuthService.getToken(code);
        KakaoUserResponse kakaoUser = kakaoOAuthService.getUserInfo(tokenResponse.accessToken());

        String jwt = authService.loginOrRegisterWithKakao(kakaoUser);
        return new JwtResponse(jwt);
    }

    @GetMapping("/me")
    public LoginMemberResponse getCurrentUser(@LoginMember Member member) {
        return new LoginMemberResponse(member.getId());
    }
}


