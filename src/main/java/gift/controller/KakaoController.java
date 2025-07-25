package gift.controller;

import gift.dto.KakaoTokenResponseDTO;
import gift.service.KakaoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

@Controller
public class KakaoController {


    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @GetMapping("/")
    @ResponseBody
    public String handleKakaoCallback(@RequestParam("code") String authorizationCode) {
        try {
            KakaoTokenResponseDTO tokenResponse = kakaoService.requestKakaoToken(authorizationCode);

            StringBuilder result = new StringBuilder();
            result.append("토큰 요청 성공\n");
            result.append("Token Type: ").append(tokenResponse.tokenType()).append("\n");
            result.append("Access Token: ").append(tokenResponse.accessToken()).append("\n");
            result.append("Expires In: ").append(tokenResponse.expiresIn()).append(" seconds\n");
            result.append("Refresh Token: ").append(tokenResponse.refreshToken()).append("\n");
            result.append("Refresh Token Expires In: ").append(tokenResponse.refreshTokenExpiresIn()).append(" seconds\n");

            if (tokenResponse.idToken() != null) {
                result.append("ID Token: ").append(tokenResponse.idToken()).append("\n");
            }
            if (tokenResponse.scope() != null) {
                result.append("Scope: ").append(tokenResponse.scope()).append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "토큰 요청 실패: " + e.getMessage();
        }
    }

}
