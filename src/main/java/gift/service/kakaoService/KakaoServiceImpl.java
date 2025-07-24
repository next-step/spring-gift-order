package gift.service.kakaoService;

import gift.dto.KakaoTokenResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class KakaoServiceImpl implements KakaoService {

    private final String clientId;
    private final String redirectUri;

    public KakaoServiceImpl(@Value("${kakao.client_id}") String clientId, @Value("${kakao.redirect_uri}") String redirectUri) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
    }

    @Override
    public String getAccessTokenFromKakao(String authorizationCode) {

        String url = "https://kauth.kakao.com/oauth/token"; // POST 요청 시, 토큰 발급 URL

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 카카오 DEVELOP 기준

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code"); // 인가코드로 고정
        body.add("client_id", clientId); // 앱 REST API 키
        body.add("redirect_uri", redirectUri); // 토큰 발급 시 응답으로 받은 refresh_token
        body.add("code", authorizationCode); // 인가 코드

        RequestEntity<MultiValueMap<String, String>> request = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(url));

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoTokenResponseDto> response = restTemplate.exchange(request, KakaoTokenResponseDto.class); // 요청 보냄

        KakaoTokenResponseDto responseBody = response.getBody();
        if (responseBody == null || responseBody.getAccessToken() == null) {
            throw new RuntimeException("카카오 토큰 요청 실패");
        }
        System.out.println(responseBody.getRefreshToken());
        System.out.println(responseBody.getRefreshTokenExpiresIn());
        System.out.println(responseBody.getExpiresIn());
        return responseBody.getAccessToken();
    }
}
