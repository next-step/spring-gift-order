package gift.service;

import gift.client.KakaoApiClient;
import org.springframework.stereotype.Service;

@Service
public class KakaoAuthService {

    private final KakaoApiClient kakaoApiClient;

    public KakaoAuthService(KakaoApiClient kakaoApiClient) {
        this.kakaoApiClient = kakaoApiClient;
    }

    public String getAccessToken(String code) {
        return kakaoApiClient.getAccessToken(code);
    }
}
