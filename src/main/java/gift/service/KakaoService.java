package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoUserInfoResponseDto;
import gift.dto.SocialTokenResponseDto;
import gift.dto.OrderRequest;
import java.net.URI;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoService {

    private final KakaoProperties properties;
    private final RestTemplate restTemplate;

    public KakaoService(KakaoProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    public String getAccessTokenFromKakao(String authorizationCode) {
        String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", properties.clientId());
        body.add("redirect_uri", properties.redirectUri());
        body.add("code", authorizationCode);

        var request = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(url));
        SocialTokenResponseDto responseDto = restTemplate.postForEntity(
            url, request, SocialTokenResponseDto.class).getBody();

        return responseDto.getAccessToken();
    }

    public KakaoUserInfoResponseDto getUserInfo(String socialAccessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(socialAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserInfoResponseDto> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            KakaoUserInfoResponseDto.class
        );

        return response.getBody();
    }

    public boolean sendMessage(String socialAccessToken, OrderRequest orderRequest) {
        JSONObject linkObj = new JSONObject();
        linkObj.put("web_url", orderRequest.getImageUrl());
        linkObj.put("mobile_web_url", orderRequest.getImageUrl());

        JSONObject templateObj = new JSONObject();
        templateObj.put("object_type", "text");
        templateObj.put("text", orderRequest.getMessage());
        templateObj.put("link", linkObj);

        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(socialAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("template_object", templateObj.toString());

        var request = new RequestEntity<>(parameters, headers, HttpMethod.POST,
            URI.create(url));

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getStatusCode().is2xxSuccessful();
    }
}
