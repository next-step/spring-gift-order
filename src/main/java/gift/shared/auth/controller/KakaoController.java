package gift.shared.auth.controller;

import gift.shared.auth.service.KakaoService;
import gift.shared.exception.token.NotAgreeException;
import gift.shared.token.status.TokenStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static gift.shared.token.status.TokenStatus.*;

@RestController
@RequestMapping("")
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
    public ResponseEntity<String> oAuthKakaoLogin(@RequestParam String code, @RequestAttribute ClientHttpResponse response){
        try{
            if(response.getStatusCode() == HttpStatus.FORBIDDEN){
                throw new NotAgreeException(NOT_AGREE.getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        kakaoService.getKakaoLoginToken(code);
        kakaoService.getUserInfo();
        return ResponseEntity.ok().build();
    }
}
