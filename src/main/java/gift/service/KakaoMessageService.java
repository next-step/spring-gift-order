package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.domain.Order;
import gift.domain.Product;
import gift.domain.ProductOption;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoMessageService{

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoMessageService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://kapi.kakao.com").build();
    }

    public void sendOrderMessage(String accessToken, Order order) {
        ProductOption option = order.getOption();
        Product product = option.getProduct();

        String message = """
            주문이 완료되었습니다!
            상품: %s
            옵션: %s
            수량: %d
            메시지: %s
            """.formatted(
                product.getName(),
                option.getName(),
                order.getQuantity(),
                order.getMessage() == null ? "-" : order.getMessage()
        );

        Map<String, Object> link = Map.of(
                "web_url", "https://your-site.com/orders/" + order.getId()
        );

        Map<String, Object> templateObject = new HashMap<>();
        templateObject.put("object_type", "text");
        templateObject.put("text", message);
        templateObject.put("link", link);
        templateObject.put("button_title", "주문 확인하기");

        try {
            String templateJson = objectMapper.writeValueAsString(templateObject);

            String result = webClient.post()
                    .uri("/v2/api/talk/memo/default/send")
                    .headers(headers -> {
                        headers.setBearerAuth(accessToken);
                        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                    })
                    .body(BodyInserters.fromFormData("template_object", templateJson))
                    .retrieve()
                    .bodyToMono(String.class)
                    .blockOptional()
                    .orElse("실패");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
