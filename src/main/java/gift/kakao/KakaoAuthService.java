package gift.kakao;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;


@Service
public class KakaoAuthService {

    @Value("${custom.kakao-client-id}")
    private String kakaoClientId;

    @Value("${custom.kakao-redirect}")
    private String redirectUri;

    @Value("${custom.kakao-auth-key}")
    private String authKey;

    public ResponseEntity<String> getAuthorization() {
        ResponseEntity<String> response = null;
        RestClient restClient = RestClient.create();

        String url = "https://kauth.kakao.com/oauth/token";
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", authKey);

        try {
            response = restClient.post()
                .uri(URI.create(url))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(String.class);
        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }

}
