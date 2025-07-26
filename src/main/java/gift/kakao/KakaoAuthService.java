package gift.kakao;

import gift.dto.response.KakaoAuthTokenResponse;
import gift.exception.KakaoTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class KakaoAuthService {

    @Value("${custom.kakao-client-id}")
    private String kakaoClientId;

    @Value("${custom.kakao-redirect}")
    private String redirectUri;

    private final RestClient kakaoRestClient;

    public KakaoAuthService(RestClient kakaoRestClient) {
        this.kakaoRestClient = kakaoRestClient;
    }


    public ResponseEntity<KakaoAuthTokenResponse> getAuthToken(String authKey) {
        ResponseEntity<KakaoAuthTokenResponse> response = null;

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", authKey);

        try {
            response = kakaoRestClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(KakaoAuthTokenResponse.class);
        }
        catch (RestClientResponseException e) {
            throw new KakaoTokenException(e.getResponseBodyAsString(), e.getStatusCode().value());
        }
        catch (Exception e) {
            throw new KakaoTokenException(e.getMessage());
        }
        return response;
    }

}
