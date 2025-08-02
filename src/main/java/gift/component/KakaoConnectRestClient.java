package gift.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.KakaoAuthTokenResponseDto;
import gift.dto.KakaoEmailResponseDto;
import gift.dto.OrderResponseDto;
import gift.exception.CustomException;
import gift.exception.ErrorCode;
import gift.properties.Properties;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoConnectRestClient implements KakaoConnectClient {

    private final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    private final RestClient client;
    private final Properties properties;

    public KakaoConnectRestClient(Properties properties){
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        client = RestClient.builder().requestFactory(requestFactory).build();
        this.properties = properties;
    }

    @Override
    public KakaoAuthTokenResponseDto retrieveToken(String code) {
        String requestUrl = properties.getAuthUrl() + "/token";
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", properties.getRestApiKey());
        body.add("redirect_uri", properties.getRedirectUri());
        body.add("code", code);

        ResponseEntity<KakaoAuthTokenResponseDto> response = client.post()
                .uri(requestUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(body)
                .retrieve()
                .toEntity(KakaoAuthTokenResponseDto.class);
        KakaoAuthTokenResponseDto result = response.getBody();
        return result;
    }

    @Override
    public String getEmail(String token) {
        String requestUrl = properties.getApiUrl() + "/user/me";

        var body = new LinkedMultiValueMap<String, String>();
        body.add("property_keys", "[\"kakao_account.email\"]");

        ResponseEntity<KakaoEmailResponseDto> response = client.post()
                .uri(requestUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        (req, res) -> {throw new CustomException(ErrorCode.KakaoAuthClientError);})
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {throw new CustomException(ErrorCode.KakaoAuthServerError);})
                .toEntity(KakaoEmailResponseDto.class);
        KakaoEmailResponseDto result = response.getBody();
        return result.getEmail();
    }

    @Override
    public void sendMessage(OrderResponseDto responseDto, String token) {
        String requestUrl = properties.getApiUrl() + "/api/talk/memo/default/send";

        var body = new LinkedMultiValueMap<String, Object>();
        String content =
                "option id: " + responseDto.optionId() + "\nquantity: " + responseDto.quantity()
                        + "\nmessage: " + responseDto.message() + "\ndate: "
                        + responseDto.orderDateTime();
        Map<String, Object> text = new HashMap<>();
        text.put("object_type", "text");
        text.put("text", content);
        text.put("link", Map.of("web_url", "http://localhost:8080",
                "mobile_web_url", "http://localhost:8080"));

        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(text);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.ParsingFailed);
        }
        body.add("template_object", json);

        client.post()
                .uri(requestUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        (req, res) -> {throw new CustomException(ErrorCode.KakaoAuthClientError);})
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {throw new CustomException(ErrorCode.KakaoAuthServerError);})
                .toBodilessEntity();
    }

    @Override
    public KakaoAuthTokenResponseDto renewalToken(String refreshToken) {
        String requestUrl = properties.getAuthUrl() + "/token";
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", properties.getRestApiKey());
        body.add("refresh_token", refreshToken);

        ResponseEntity<KakaoAuthTokenResponseDto> response = client.post()
                .uri(requestUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        (req, res) -> {throw new CustomException(ErrorCode.KakaoAuthClientError);})
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {throw new CustomException(ErrorCode.KakaoAuthServerError);})
                .toEntity(KakaoAuthTokenResponseDto.class);
        return response.getBody();
    }
}
