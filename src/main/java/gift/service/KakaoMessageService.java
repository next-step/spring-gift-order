package gift.service;

import gift.Entity.Member;
import gift.Entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoMessageService {

    private final RestTemplate restTemplate;
    private final Logger log = LoggerFactory.getLogger(KakaoMessageService.class);

    public KakaoMessageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendMessageToMyself(Member member, Order order, String accessToken) {
        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Kakao 메시지 JSON 포맷 생성
        String json = "{"
                + "\"object_type\":\"text\","
                + "\"text\":\"[주문 완료]\\n상품: " + order.getOption().getProduct().getName()
                + "\\n옵션: " + order.getOption().getName()
                + "\\n수량: " + order.getQuantity()
                + "\\n요청사항: " + order.getMessage() + "\","
                + "\"link\":{\"web_url\":\"https://your-site.com\",\"mobile_web_url\":\"https://your-site.com\"}"
                + "}";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", json);

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(URI.create(url), entity, String.class);
            log.info("카카오 메시지 응답: {}", response.getBody());
        } catch (Exception e) {
            log.error("카카오톡 메시지 전송 실패", e);
        }
    }

}

