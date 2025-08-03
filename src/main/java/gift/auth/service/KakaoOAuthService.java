package gift.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.dto.KakaoErrorResponseDto;
import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.dto.KakaoUserResponseDto;
import gift.auth.exception.KakaoAuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

@Service
public class KakaoOAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-url}")
    private String tokenUrl;

    @Value("${kakao.profile-url}")
    private String profileUrl;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public KakaoOAuthService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public KakaoTokenResponseDto requestAccessToken(String authorizationCode) {
        URI tokenUri = URI.create(tokenUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", authorizationCode);
        body.add("scope", "talk_message");

        RequestEntity<MultiValueMap<String, String>> request = RequestEntity
                .post(tokenUri)
                .headers(headers)
                .body(body);

        try {
            ResponseEntity<KakaoTokenResponseDto> response =
                    restTemplate.exchange(request, KakaoTokenResponseDto.class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            String json = e.getResponseBodyAsString();
            KakaoErrorResponseDto kakaoErrorResponseDto;
            try {
                kakaoErrorResponseDto = objectMapper.readValue(json, KakaoErrorResponseDto.class);
            } catch (IOException io) {
                throw new KakaoAuthException("카카오 에러 바디 파싱 실패", e);
            }

            switch (kakaoErrorResponseDto.getError()) {
                case "invalid_request":
                    throw new KakaoAuthException("잘못된 파라미터입니다. : " + kakaoErrorResponseDto.getErrorDescription());
                case "invalid_client":
                    throw new KakaoAuthException("앱 키가 올바르지 않습니다. 설정을 확인하세요. ");
                case "invalid_grant":
                    throw new KakaoAuthException("인가 코드가 만료되었거나 잘못되었습니다. 다시 로그인해주세요. ");
                case "invalid_scope":
                    throw new KakaoAuthException("잘못된 동의 항목 ID입니다. ");
                case "misconfigured":
                    throw new KakaoAuthException("플랫폼 설정이 올바르지 않습니다. 카카오 개발자 콘솔을 확인하세요. ");
                case "access_denied":
                    throw new KakaoAuthException("사용자가 로그인/동의를 취소했습니다.");
                case "server_error":
                    throw new KakaoAuthException("카카오 서버 오류입니다. 잠시 후 다시 시도해주세요. ");
                default:
                    throw new KakaoAuthException(
                            "카카오 토큰 요청 실패: " + kakaoErrorResponseDto.getError() + " / " + kakaoErrorResponseDto.getErrorDescription()
                    );
            }
        }
    }

    public KakaoUserResponseDto requestUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                profileUrl,
                HttpMethod.GET,
                request,
                String.class
        );

        String json = response.getBody();

        JsonNode root = objectMapper.readTree(json);
        String email    = root.path("kakao_account").path("email").asText();
        String nickname = root.path("properties").path("nickname").asText();

        return new KakaoUserResponseDto(email, nickname);
    }
}
