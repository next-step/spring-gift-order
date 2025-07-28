package gift.service.auth;

import gift.config.KakaoProperties;
import gift.dto.kakao.KakaoTokenResponse;
import gift.global.util.KakaoAuthClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

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


