package gift.service.auth;

import gift.global.util.KakaoAuthClient;
import org.springframework.stereotype.Service;

@Service
public class KakaoAuthService {

    private final KakaoAuthClient kakaoAuthClient;

    public KakaoAuthService(KakaoAuthClient kakaoAuthClient) {
        this.kakaoAuthClient = kakaoAuthClient;
    }

    public String getAccessToken(String authorizationCode) {
        var responseBody = kakaoAuthClient.requestAccessToken(authorizationCode);
        return responseBody.accessToken();
    }
}


