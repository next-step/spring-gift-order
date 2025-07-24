package gift.authorization.oauth;

import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoOAuthService {

    private final KakaoOAuthProperties kakaoProps;
    private final RestTemplate restTemplate = new RestTemplate();

    public KakaoOAuthService(KakaoOAuthProperties kakaoProps) {
        this.kakaoProps = kakaoProps;
    }

    public String getKakaoLoginUrl() {
        return "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + kakaoProps.getClientId() +
                "&redirect_uri=" + kakaoProps.getRedirectUri();
    }

    public KakaoTokenResponseDto requestAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProps.getClientId());
        params.add("redirect_uri", kakaoProps.getRedirectUri());
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponseDto> response = restTemplate.exchange(
                kakaoProps.getTokenUri(),
                HttpMethod.POST,
                request,
                KakaoTokenResponseDto.class
        );

        return response.getBody();
    }
}
