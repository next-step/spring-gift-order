package gift.controller;

import gift.dto.KakaoTokenResponse;
import gift.service.KakaoApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class KakaoController {

    private final KakaoApiService kakaoApiService;

    KakaoController(KakaoApiService kakaoApiService) {
        this.kakaoApiService = kakaoApiService;
    }

    @GetMapping("/kakao/login")
    public RedirectView authorize() {
        return new RedirectView(kakaoApiService.getAuthUrl());
    }

    @GetMapping
    public ResponseEntity<KakaoTokenResponse> getToken(@RequestParam String code) {
        return new ResponseEntity<>(kakaoApiService.getToken(code), HttpStatus.OK);
    }
}
