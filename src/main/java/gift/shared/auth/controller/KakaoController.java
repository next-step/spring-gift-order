package gift.shared.auth.controller;

import gift.shared.auth.dto.response.TokenResponse;
import gift.shared.auth.service.KakaoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
public class KakaoController {
    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @GetMapping("/kakao/login")
    public ResponseEntity<String> oAuthKakaoRedirect(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("location", kakaoService.getAuthorizationCode());
        return new ResponseEntity<String>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping()
    public ResponseEntity<TokenResponse> oAuthKakaoLogin(@RequestParam String code) throws NoSuchAlgorithmException {
        return ResponseEntity.ok().body(kakaoService.kakaoLogin(code));
    }
}
