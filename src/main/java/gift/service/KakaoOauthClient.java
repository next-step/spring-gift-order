package gift.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.properties.KakaoProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@Component
public class KakaoOauthClient {

    private static final Logger log = LoggerFactory.getLogger(KakaoOauthClient.class);
    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public KakaoOauthClient(KakaoProperties kakaoProperties,
                            RestTemplate restTemplate,
                            ObjectMapper objectMapper) {
        this.kakaoProperties = kakaoProperties;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getAccessToken(String code) throws Exception {
        String tokenUrl = kakaoProperties.getAuthBaseUrl() + kakaoProperties.getTokenPath();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUri());
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(URI.create(tokenUrl), request, String.class);

        log.info("카카오 토큰 응답: {}", response.getBody());

        JsonNode node = objectMapper.readTree(response.getBody());
        return node.get("access_token").asText();
    }

    public JsonNode getUserInfo(String accessToken) throws Exception {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);

        log.info("사용자 정보 응답: {}", response.getBody());

        return objectMapper.readTree(response.getBody());
    }
}

