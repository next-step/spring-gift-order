package gift.authorization.oauth;

import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import gift.authorization.oauth.exception.KakaoLoginRequestException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class KakaoClient {

    private final RestClient restClient;
    private final KakaoOAuthProperties kakaoProps;

    public KakaoClient(RestClient restClient, KakaoOAuthProperties kakaoProps) {
        this.restClient = restClient;
        this.kakaoProps = kakaoProps;
    }

    public KakaoTokenResponseDto requestToken(String tokenUri, String clientId, String redirectUri, String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);


        try {
            return restClient.post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(KakaoTokenResponseDto.class);
        } catch (Exception e){
            throw new KakaoLoginRequestException("카카오 토큰 발급 실패");
        }
    }

    public String requestUserId(String accessToken) {
        try {
            return restClient.get()
                    .uri(kakaoProps.getUserInfoUri())
                    .headers(http -> http.setBearerAuth(accessToken))
                    .retrieve()
                    .body(Map.class)
                    .get("id").toString();
        } catch (Exception e) {
            throw new KakaoLoginRequestException("UserId 추출 실패");
        }
    }

    public KakaoTokenResponseDto refreshAccessToken(String tokenUri, String clientId, String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("refresh_token", refreshToken);

        try {
            return restClient.post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(KakaoTokenResponseDto.class);
        } catch (Exception e){
            throw new KakaoLoginRequestException("카카오 토큰 갱신 실패");
        }
    }
}
