package gift.kakao;
import gift.kakao.KakaoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @GetMapping("/token")
    public String getToken(@RequestParam("code") String code) {
        return kakaoService.getAccessToken(code);
    }
}
