package gift.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.KakaoProperties;
import gift.dto.login.KakaoErrorDto;
import gift.dto.login.KakaoProfileDto;
import gift.dto.login.KakaoTokenDto;
import gift.exception.KakaoTokenFetchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class KakaoClient {

    private final KakaoProperties kakaoProperties;

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    public KakaoClient(KakaoProperties kakaoProperties, ObjectMapper objectMapper) {
        this.kakaoProperties = kakaoProperties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create();
    }

    public KakaoTokenDto fetchToken(String code) {
        String baseUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE,
            "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.restApiKey());
        body.add("redirect_uri", kakaoProperties.redirectUri());
        body.add("code", code);

        try {
            ResponseEntity<KakaoTokenDto> response = restClient.post()
                .uri(baseUrl)
                .headers(h -> h.addAll(headers))
                .body(body)
                .retrieve()
                .toEntity(KakaoTokenDto.class);

            return response.getBody();

        } catch (RestClientResponseException exception) {
            String json = exception.getResponseBodyAsString();
            KakaoErrorDto errorDto = null;
            try {
                errorDto = objectMapper.readValue(json,
                    KakaoErrorDto.class);
            } catch (JsonProcessingException e) {
                throw new KakaoTokenFetchException("카카오 오류 응답에 대한 파싱을 실패하였습니다.");
            }

            throw new KakaoTokenFetchException(errorDto.code(), errorDto.message());
        }
    }

    public KakaoProfileDto fetchProfile(String token) {
        String baseUrl = "https://kapi.kakao.com/v2/user/me";
        String bearerToken = "Bearer " + token;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, bearerToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        ResponseEntity<KakaoProfileDto> response = restClient.post()
            .uri(baseUrl)
            .headers(h -> h.addAll(headers))
            .retrieve()
            .toEntity(KakaoProfileDto.class);

        return response.getBody();
    }
}