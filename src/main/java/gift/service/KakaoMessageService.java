package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gift.domain.Order;
import gift.domain.ProductOption;
import gift.repository.ProductOptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoMessageService {

    private static final Logger log = LoggerFactory.getLogger(KakaoMessageService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductOptionRepository productOptionRepository;

    public KakaoMessageService(ProductOptionRepository productOptionRepository) {
        this.productOptionRepository = productOptionRepository;
    }

    public void sendOrderMessageToMe(String accessToken, Order order) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("유효한 카카오 access token이 필요합니다.");
        }

        ProductOption option = productOptionRepository.findById(order.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다."));

        String messageText = """
            주문이 완료되었어요!
            - 옵션: %s
            - 수량: %d개
            - 메시지: %s
            """.formatted(option.getName(), order.getQuantity(), order.getMessage());

        ObjectNode templateObject = objectMapper.createObjectNode();
        templateObject.put("object_type", "text");
        templateObject.put("text", messageText);

        ObjectNode link = templateObject.putObject("link");
        link.put("web_url", "http://localhost:8080/api/orders");
        link.put("mobile_web_url", "http://localhost:8080/api/orders");

        templateObject.put("button_title", "주문 확인하기");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("template_object", templateObject.toString());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://kapi.kakao.com/v2/api/talk/memo/default/send",
                    entity,
                    String.class
            );
            log.info("카카오톡 메시지 전송 성공: {}", response.getBody());
        } catch (Exception e) {
            log.error("카카오톡 메시지 전송 실패", e);
            throw new RuntimeException("카카오톡 메시지 전송 실패: " + e.getMessage(), e);
        }
    }
}


