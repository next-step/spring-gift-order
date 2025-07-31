package gift.client;

import gift.dto.kakao.KakaoTokenRequest;
import gift.dto.kakao.KakaoTokenResponse;
import gift.dto.kakao.KakaoUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoApiClient {

    private final RestTemplate restTemplate;

    @Value("${kakao.provider.token-uri}")
    private String tokenUrl;
    @Value("${kakao.provider.user-info-uri}")
    private String userInfoUrl;
    @Value("${kakao.provider.message-memo-uri}")
    private String messageMemoUrl;

    public KakaoApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchAccessToken(KakaoTokenRequest tokenRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(tokenRequestDto.toMultiValueMap(), headers);
        KakaoTokenResponse response = restTemplate.postForObject(tokenUrl, request, KakaoTokenResponse.class);
        if (response == null || response.getAccessToken() == null) {
            throw new IllegalStateException("카카오 액세스 토큰 조회에 실패했습니다.");
        }
        return response.getAccessToken();
    }

    public KakaoUserInfoResponse fetchUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, KakaoUserInfoResponse.class).getBody();
    }

    public void sendMemoToSelf(String accessToken, String templateJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateJson);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(messageMemoUrl, request, String.class);
    }
}