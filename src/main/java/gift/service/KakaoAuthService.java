package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.KakaoProperties;
import gift.dto.kakao.KakaoUserInfo;
import gift.dto.response.KaKaoTokenResponseDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Service
public class KakaoAuthService {
    private final KakaoProperties kakaoProperties;
    private final RestClient restClient;


    public KakaoAuthService(KakaoProperties kakaoProperties, RestClient restClient) {
        this.kakaoProperties = kakaoProperties;
        this.restClient = restClient;
    }

    public String getAuthorizeUrl() {
        return "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + kakaoProperties.getClientId()
                + "&redirect_uri=" + kakaoProperties.getRedirectUri();
    }
    public KaKaoTokenResponseDto requestToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUri());
        body.add("code", code);

        return restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(KaKaoTokenResponseDto.class);
    }

    public KakaoUserInfo parseIdToken(String idToken) {
        String[] parts = idToken.split("\\.");
        String payload = parts[1];
        String decoded = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(decoded, KakaoUserInfo.class);  // email, sub 포함
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
