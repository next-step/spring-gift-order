package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenRequestDto;
import gift.dto.KakaoTokenResponseDto;
import gift.exception.KakaoAuthenticationException;
import gift.exception.KakaoClientError;
import gift.exception.KakaoConnectionException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
public class KakaoAuthService {
    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 3000;
    private static final int READ_TIMEOUT_MILLISECONDS = 3000;
    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com";
    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;

    public KakaoAuthService(KakaoProperties kakaoProperties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT_MILLISECONDS);
        requestFactory.setReadTimeout(READ_TIMEOUT_MILLISECONDS);

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(KAKAO_AUTH_URL)
                .build();
        this.kakaoProperties = kakaoProperties;
    }

    public String getAccessToken(String authorizeCode) {
        KakaoTokenRequestDto body = new KakaoTokenRequestDto(
                kakaoProperties.getClientId(),
                kakaoProperties.getRedirectUri(),
                authorizeCode
        );

        try {
            KakaoTokenResponseDto response = restClient.post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body.dtoToFormData())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, res) -> {
                        KakaoClientError error = KakaoClientError.from(res.getStatusCode());
                        String errorMessage = error.getMessage();
                        throw new KakaoAuthenticationException(errorMessage + "응답 코드: " + res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, res) -> {
                        throw new KakaoConnectionException("카카오 서버에 오류가 발생했습니다. 응답 코드: " + res.getStatusCode());
                    })
                    .body(KakaoTokenResponseDto.class);

            return response.accessToken();
        } catch (ResourceAccessException e) {
            throw new KakaoConnectionException("카카오 서버와 통신이 원활하지 않습니다.", e);
        }
    }
}
