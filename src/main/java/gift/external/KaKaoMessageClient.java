package gift.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.common.code.CustomResponseCode;
import gift.common.exception.KaKaoClientException;
import gift.common.exception.ServerErrorException;
import gift.entity.member.Member;
import gift.entity.order.Order;
import gift.entity.product.Product;
import gift.entity.product.option.ProductOption;
import gift.service.auth.AuthService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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

        if (trySendTemplate(template, member.getAccessToken())) {
            return;
        }

        String newAccessToken = authService.refreshAccessToken(member.getRefreshToken());
        if (!trySendTemplate(template, newAccessToken)) {
            throw new KaKaoClientException(CustomResponseCode.KAKAO_MESSAGE_SEND_FAILED);
        }
    }

    private boolean trySendTemplate(String template, String accessToken) {
        try {
            sendTemplate(template, accessToken);
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return false;
            }
            throw new KaKaoClientException(CustomResponseCode.KAKAO_MESSAGE_SEND_FAILED);
        } catch (HttpServerErrorException e) {
            throw new ServerErrorException(CustomResponseCode.SERVER_ERROR);
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
