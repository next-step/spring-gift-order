package gift.controller;

import gift.dto.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/oauth/kakao")
public class KakaoOAuthController {

    private final RestClient restClient = RestClient.create();

    @Value("${kakao.oauth.client-id}")
    private String clientId;

    @Value("${kakao.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.oauth.auth-url}")
    private String authUrl;

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        URI uri = UriComponentsBuilder.fromUriString(authUrl)
                .queryParam("scope", "talk_message")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .build().toUri();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(uri)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<KakaoTokenResponse> kakaoCallback(@RequestParam("code") String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        ResponseEntity<KakaoTokenResponse> response = restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toEntity(KakaoTokenResponse.class);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }
}