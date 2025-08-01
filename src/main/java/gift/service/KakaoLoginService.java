package gift.service;

import gift.client.KakaoAuthApiClient;
import gift.config.KakaoProperties;
import gift.dto.KakaoLoginRequest;
import gift.dto.KakaoLoginResponse;
import gift.dto.KakaoTokenRequest;
import gift.dto.TokenResponse;
import org.springframework.stereotype.Service;

@Service
public class KakaoLoginService {

    private final KakaoProperties kakaoProperties;
    private final KakaoAuthApiClient kakaoAuthApiClient;

    public KakaoLoginService(KakaoProperties kakaoProperties,
        KakaoAuthApiClient kakaoAuthApiClient) {
        this.kakaoProperties = kakaoProperties;
        this.kakaoAuthApiClient = kakaoAuthApiClient;
    }

    public TokenResponse getAccessToken(KakaoLoginRequest request) {
        KakaoTokenRequest tokenRequest = new KakaoTokenRequest(
            "authorization_code",
            kakaoProperties.clientId(),
            kakaoProperties.redirectUri(),
            request.authorizationCode(),
            kakaoProperties.clientSecret()
        );

        KakaoLoginResponse response = kakaoAuthApiClient.getAccessToken(tokenRequest);

        if (response == null) {
            throw new RuntimeException("Empty response");
        }

        if (response.access_token() == null) {
            throw new RuntimeException("Access token not found");
        }

        return new TokenResponse(response.access_token());
    }

}
