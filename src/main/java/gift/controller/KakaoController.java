package gift.controller;

import gift.dto.KakaoTokenResponseDTO;
import gift.dto.TokenResponseDTO;
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
    public ResponseEntity<TokenResponseDTO> handleKakaoCallback(@RequestParam("code") String authorizationCode) {
        TokenResponseDTO tokenResponse = kakaoService.loginWithKakao(authorizationCode);
        return ResponseEntity.ok(tokenResponse);
    }
}
