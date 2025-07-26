package gift.kakao;

import gift.exception.KakaoTokenException;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class KakaoAuthService {

    @Value("${custom.kakao-client-id}")
    private String kakaoClientId;

    @Value("${custom.kakao-redirect}")
    private String redirectUri;

    private RestClient kakaoRestClient;

    public KakaoAuthService(RestClient kakaoRestClient) {
        this.kakaoRestClient = kakaoRestClient;
    }


    public ResponseEntity<String> getAuthorization(String authKey) {
        ResponseEntity<String> response = null;

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
                .toEntity(String.class);
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
