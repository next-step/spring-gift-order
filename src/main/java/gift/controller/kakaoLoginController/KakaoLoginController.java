package gift.controller.kakaoLoginController;


import gift.service.kakaoService.KakaoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoLoginController {

    private final KakaoService kakaoService;

    public KakaoLoginController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    /***
     * 지금 전체적 흐름
     * 1. http://localhost:8080/login/page 접속
     * 2. https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri로 자동 리다이렉트 -> 이게 카카오서버에서 인가코드 생성해줌
     */
    @GetMapping("/login/page")
    public ResponseEntity<Void> redirectToKakao() {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
        return ResponseEntity.status(302).header("Location", location).build();
    }

    /***
     * 어차피 http://localhost:8080/?code=... 로 리다이렉트 해주니까 매핑을 없애주면 될 거 같은데
     */
    @GetMapping
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        System.out.println(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}