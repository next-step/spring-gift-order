package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.KakaoTokenResponse;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import gift.entity.vo.Email;
import gift.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoApiService {

    private final static String TEMPLATE_ID = "122911";

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.kauth-host}")
    private String kauthHost;

    @Value("${kakao.kapi-host}")
    private String kapiHost;

    private ObjectMapper objectMapper;

    private final RestClient restClient;

    public KakaoApiService(RestClient restClient) {
        this.restClient = restClient;
        this.objectMapper = new ObjectMapper();
    }

    public String getAuthUrl() {
        return UriComponentsBuilder
                .fromUriString(kauthHost)
                .path("/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "talk_message,openid,account_email")
                .build()
                .toString();
    }

    public KakaoTokenResponse getToken(String code) {
        LinkedMultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("grant_type", "authorization_code");
        request.add("client_id", clientId);
        request.add("redirect_uri", redirectUri);
        request.add("code", code);

        return restClient.post()
                .uri(kauthHost + "/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(request)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }

    public Email getEmail(String accessToken) {
        String jsonResponse = restClient.post()
                .uri(kapiHost + "/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            String email = root.path("kakao_account").path("email").asText();
            return new Email(email);
        } catch (JsonProcessingException e) {
            throw new ExternalApiException("카카오 사용자 조회에 실패했습니다.");
        }
    }

    public String getTemplateArgs(Order order) {
        Option option = order.getOption();
        Product product = option.getProduct();

        Map<String, String> templateArgs = new HashMap<>();
        templateArgs.put("image_url", product.getImageUrl());
        templateArgs.put("product_name", product.getName() + " - " + option.getName().value());
        templateArgs.put("price", order.getPrice().toString());
        templateArgs.put("message", order.getMessage());

        try {
            return objectMapper.writeValueAsString(templateArgs);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("문자열로 변환 실패");
        }
    }

    public void sendMessage(Order order, String accessToken) {
        Option option = order.getOption();
        Product product = option.getProduct();

        LinkedMultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("template_id", TEMPLATE_ID);
        request.add("template_args", getTemplateArgs(order));

        restClient.post()
                .uri(kapiHost + "/v2/api/talk/memo/send")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
