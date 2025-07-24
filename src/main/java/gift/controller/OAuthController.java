package gift.controller;

import gift.config.KakaoOauthProperties;
import gift.dto.kakao.KakaoTokenResponse;
import gift.dto.kakao.KakaoUserInfoResponse;
import gift.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/oauth")
public class OAuthController {

    private final KakaoOauthProperties kakaoOauthProperties;
    private final OAuthService oAuthService;

    public OAuthController(KakaoOauthProperties kakaoOauthProperties, OAuthService oAuthService) {
        this.kakaoOauthProperties = kakaoOauthProperties;
        this.oAuthService = oAuthService;
    }

    @GetMapping("/kakao")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        String url = "https://kauth.kakao.com/oauth/authorize?"
                + "client_id=" + kakaoOauthProperties.getClientId()
                + "&redirect_uri=" + kakaoOauthProperties.getRedirectUri()
                + "&response_type=code"
                + "&scope=talk_message";

        response.sendRedirect(url);
    }

    @GetMapping("/kakao/callback")
    @ResponseBody
    public ResponseEntity<KakaoUserInfoResponse> kakaoCallback(@RequestParam("code") String code) {
        KakaoTokenResponse tokenResponse = oAuthService.getKakaoToken(code);

        KakaoUserInfoResponse userInfoResponse = oAuthService.getKakaoUserInfo(
                tokenResponse.accessToken());

        return ResponseEntity.ok(userInfoResponse);
    }
}
