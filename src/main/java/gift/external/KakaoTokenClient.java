package gift.external;

import gift.common.exception.KakaoAuthorizationException;
import gift.dto.external.KakaoAuthErrorResponse;
import gift.dto.external.KakaoPublicKeyResponse;
import gift.dto.external.KakaoTokenRequest;
import gift.dto.external.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoTokenClient {
    private final String redirectUri;
    private final String restApiKey;
    private final String baseUrl;
    private final RestClient restClient;

    public KakaoTokenClient(
            @Value("${gift.oauth.redirect-uri}") String baseRedirectUri,
            @Value("${gift.oauth.provider.kakao.baseurl}") String baseUrl,
            @Value("${gift.oauth.provider.kakao.rest-api-key}") String restApiKey,
            RestClient restClient
    ) {
        this.redirectUri = baseRedirectUri + "/kakao";
        this.restApiKey = restApiKey;
        this.baseUrl = baseUrl;
        this.restClient = restClient;
    }

    private MultiValueMap<String, String> convertToMultiValueMap(KakaoTokenRequest request) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", request.grantType());
        map.add("client_id", request.clientId());
        map.add("redirect_uri", request.redirectUri());
        map.add("code", request.code());
        return map;
    }

    public KakaoPublicKeyResponse getPublicKeyResponse() {
        return restClient
                .get()
                .uri(baseUrl + "/.well-known/jwks.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(KakaoPublicKeyResponse.class);
    }

    public KakaoTokenResponse getTokenResponse(String code) {
        var requestBody = new KakaoTokenRequest(
                "authorization_code",
                restApiKey,
                redirectUri,
                code
        );
        return restClient
                .post()
                .uri(baseUrl + "/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(convertToMultiValueMap(requestBody))
                .exchange((req, res)-> {
                    if (res.getStatusCode().is4xxClientError() || res.getStatusCode().is5xxServerError()) {
                        int statusCode = res.getStatusCode().value();
                        KakaoAuthErrorResponse errorResponse = res.bodyTo(KakaoAuthErrorResponse.class);

                        if (errorResponse == null || errorResponse.errorCode() == null) {
                            throw new KakaoAuthorizationException(
                                    HttpStatus.valueOf(statusCode),
                                    "UNKNOWN_CODE",
                                    "카카오 인증 서버에서 토큰을 받는 중 알수 없는 에러가 발생했습니다."
                            );
                        }

                        throw new KakaoAuthorizationException(
                                HttpStatus.valueOf(statusCode),
                                errorResponse.errorCode(),
                                errorResponse.errorDescription()
                        );
                    }
                    return res.bodyTo(KakaoTokenResponse.class);
                });
    }
}
