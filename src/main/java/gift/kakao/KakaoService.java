package gift.kakao;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Service
public class KakaoService {

    private final KakaoProperties properties;
    private final RestTemplate restTemplate;

    public KakaoService(KakaoProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
    }


    public String getAccessToken(String authorizationCode) {
        String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", properties.getClientId());
        body.add("redirect_uri", properties.getRedirectUri());
        body.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        try {
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println("토큰 요청 실패!");
            System.out.println("Status Code: " + e.getStatusCode());
            System.out.println("Response Body: " + e.getResponseBodyAsString());
            System.out.println(authorizationCode);
            throw e;
        }
    }


}
