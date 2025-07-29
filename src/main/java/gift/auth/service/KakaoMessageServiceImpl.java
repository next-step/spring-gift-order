package gift.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.order.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KakaoMessageServiceImpl implements KakaoMessageService {
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${kakao.api.memo-url}")
    private String memoUrl;

    public KakaoMessageServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendOrderMemo(String accessToken, Order order) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=UTF-8"));

        var content = Map.of(
                "title",  "주문이 완료되었습니다!",
                "description", "주문번호: " + order.getId(),
                "link", Map.of(
                        "web_url",  "http://localhost:8080/orders/" + order.getId(),
                        "mobile_web_url", "http://localhost:8080/orders/" + order.getId()
                )
        );
        var feed = Map.of(
                "object_type", "feed",
                "content",  content
        );

        String templateObject;
        try {
            templateObject = objectMapper.writeValueAsString(feed);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("카카오 메시지 JSON 변환 실패", e);
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateObject);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(memoUrl, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("카카오톡 API 호출 실패: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("카카오톡 API 호출 실패: " + e.getStatusCode(), e);
        }
    }
}
