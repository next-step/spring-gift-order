package gift.service;

import gift.auth.JwtTokenProvider;
import gift.client.KakaoApiClient;
import gift.dto.kakao.KakaoTokenRequest;
import gift.dto.kakao.KakaoUserInfoResponse;
import gift.dto.kakao.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KakaoService {

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoApiClient kakaoApiClient;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public KakaoService(JwtTokenProvider jwtTokenProvider, KakaoApiClient kakaoApiClient) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.kakaoApiClient = kakaoApiClient;
    }

    public LoginResponse processKakaoLogin(String code) {
        KakaoTokenRequest tokenRequest = KakaoTokenRequest.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .code(code)
                .build();

        String accessToken = kakaoApiClient.fetchAccessToken(tokenRequest);

        KakaoUserInfoResponse userInfo = kakaoApiClient.fetchUserInfo(accessToken);

        String serviceToken = jwtTokenProvider.createToken(userInfo.id().toString());

        return new LoginResponse(userInfo.getId(), userInfo.getNickname(), serviceToken);
    }
}