package gift.service;

import gift.auth.JwtTokenProvider;
import gift.dto.kakao.KakaoTokenRequest;
import gift.dto.kakao.KakaoTokenResponse;
import gift.dto.kakao.KakaoUserInfoResponse;
import gift.dto.kakao.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoService {

    private final RestTemplate restTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client.id}")
    private String clientId;

    public KakaoService(RestTemplate restTemplate, JwtTokenProvider jwtTokenProvider) {
        this.restTemplate = restTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse processKakaoLogin(String code) {
        String accessToken = getAccessToken(code);
        KakaoUserInfoResponse userInfo = getUserInfo(accessToken);
        String token = jwtTokenProvider.createToken(userInfo.getId().toString());
        return new LoginResponse(userInfo.getId(), userInfo.getNickname(), token);
    }

    private String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        KakaoTokenRequest requestDto = KakaoTokenRequest.builder()
                .clientId(clientId)
                .redirectUri("http://localhost:8080")
                .code(code)
                .build();

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(requestDto.toMultiValueMap(), headers);

        KakaoTokenResponse tokenResponse = restTemplate.postForObject(tokenUrl, tokenRequest, KakaoTokenResponse.class);

        return tokenResponse.getAccessToken();
    }

    private KakaoUserInfoResponse getUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> userInfoRequest = new HttpEntity<>(headers);
        return restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, userInfoRequest, KakaoUserInfoResponse.class
        ).getBody();
    }
}