package gift.shared.auth.service;

import gift.shared.auth.dto.response.KakaoBasicInfoResponse;
import gift.shared.auth.dto.response.KakaoTokenResponse;
import gift.shared.exception.token.NoTokenException;
import gift.shared.exception.user.NoUserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static gift.shared.token.status.TokenStatus.*;
import static org.springframework.http.MediaType.*;

@Service
public class KakaoService {
    @Value("${kakao-client-id}")
    private String kakaoLoginClientId;

    @Value("${kakao-redirect-url}")
    private String redirectUrl;

    private final String baseURL = "https://kauth.kakao.com";

    private KakaoTokenResponse kakaoToken;

    private final RestClient restClient = RestClient.builder()
            .baseUrl(baseURL)
            .build();

    private final RestClient kakaoInfoClient = RestClient.builder()
            .baseUrl("https://kapi.kakao.com/")
            .build();

    public KakaoService() {}

    public String getAuthorizationCode(){
        return baseURL + "/oauth/authorize?scope=talk_message&response_type=code&redirect_uri="
                + redirectUrl
                +"&client_id="
                + kakaoLoginClientId;
    }

    public void getKakaoLoginToken(String accessCode){
        String url = "/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", kakaoLoginClientId);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUrl);
        params.add("code", accessCode);

        try{
            ResponseEntity<KakaoTokenResponse> response = restClient.post()
                    .uri(url)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .accept(APPLICATION_JSON)
                    .body(params)
                    .retrieve()
                    .toEntity(KakaoTokenResponse.class);
            if(response.getBody() != null){
                this.kakaoToken = response.getBody();
            }

        }catch(HttpClientErrorException e){
            throw new NoUserException(e.getMessage());
        }
    }

    public String getUserInfo(){
        String url = "/v2/user/me";

        if(kakaoToken.access_token() == null){
            throw new NoTokenException(NO_TOKEN.getMessage());
        }

        try {
            ResponseEntity<String> response = kakaoInfoClient.get()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(APPLICATION_FORM_URLENCODED))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoToken.access_token())
                    .accept(APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class);
            System.out.println(response.getBody());
        }catch(HttpClientErrorException e) {
            throw new NoUserException(e.getMessage());
        }
        return "";
    }
}
