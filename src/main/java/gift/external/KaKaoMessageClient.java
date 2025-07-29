package gift.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.common.code.CustomResponseCode;
import gift.common.exception.KaKaoClientException;
import gift.common.exception.UnauthorizedException;
import gift.entity.Member;
import gift.entity.Order;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.service.Auth.AuthService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KaKaoMessageClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final AuthService authService;

    @Value("${kakao.message-url}")
    private String messageUrl;

    public KaKaoMessageClient(RestClient.Builder restClientBuilder, ObjectMapper objectMapper,
        AuthService authService) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.authService = authService;
    }

    public void sendOrderMessage(Order order, Member member) {
        String template = createOrderMessageTemplate(order);

        try {
            sendTemplate(template, member.getAccessToken());
        } catch (UnauthorizedException e) {
            String refreshToken = member.getRefreshToken();
            String newAccessToken = authService.refreshAccessToken(refreshToken);
            sendTemplate(template, newAccessToken);
        }
    }

    private void sendTemplate(String template, String accessToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", template);

        restClient.post()
            .uri(messageUrl)
            .headers(headers -> {
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.setBearerAuth(accessToken);
            })
            .body(body)
            .retrieve()
            .onStatus(status -> status.value() == 401, (req, res) -> {
                throw new UnauthorizedException();
            })
            .onStatus(HttpStatusCode::is4xxClientError,
                (req, res) -> {
                    throw new KaKaoClientException(
                        CustomResponseCode.KAKAO_MESSAGE_SEND_FAILED);
                })
            .toBodilessEntity();
    }

    private String createOrderMessageTemplate(Order order) {
        ProductOption option = order.getProductOption();
        Product product = option.getProduct();

        String text = """
            상품 주문이 완료되었습니다!

            상품: %s
            옵션: %s
            수량: %d
            메시지: %s
            """.formatted(
            product.getName(),
            option.getName(),
            order.getQuantity(),
            order.getMessage()
        ).strip();

        Map<String, Object> template = Map.of(
            "object_type", "text",
            "text", text,
            "link", Map.of(
                "web_url", "https://docstory.kr",
                "mobile_web_url", "https://docstory.kr"
            ),
            "button_title", "주문 확인"
        );

        try {
            return objectMapper.writeValueAsString(template);
        } catch (JsonProcessingException e) {
            throw new KaKaoClientException(CustomResponseCode.KAKAO_MESSAGE_SEND_FAILED);
        }
    }
}
