package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoResponse;
import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoService {
    private final KakaoProperties kakaoProperties;

    public KakaoService(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }

    public String getKakaoToken(String code) {
        KakaoResponse response = WebClient.create(kakaoProperties.getOauthTokenUrlHost()).post()
                .uri("/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=authorization_code&client_id=" + kakaoProperties.getClientId() 
                         + "&redirect_uri=" + kakaoProperties.getRedirectUri() + "&code=" + code)
                .retrieve()
                .bodyToMono(KakaoResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();

        return response.accessToken();
    }

}