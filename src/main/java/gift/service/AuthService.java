package gift.service;

import gift.config.KakaoProperties;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final KakaoService kakaoService;
    private final KakaoProperties kakaoProperties;

    public AuthService(KakaoService kakaoService, KakaoProperties kakaoProperties) {
        this.kakaoService = kakaoService;
        this.kakaoProperties = kakaoProperties;
    }

    public String getKakaoAuthorizationUrl() {
        return kakaoProperties.buildAuthorizationUrl();
    }

    public String processKakaoLogin(String accessCode) {
        return kakaoService.getKakaoToken(accessCode);
    }
}
