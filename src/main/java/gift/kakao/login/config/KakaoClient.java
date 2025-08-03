package gift.kakao.login.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.global.exception.KakaoMessageSendException;
import gift.global.exception.KakaoMessageSerializationException;
import gift.kakao.order.entity.Order;
import gift.kakao.login.config.KakaoProperties;
import gift.option.entity.Option;
import gift.product.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

@Component
public class KakaoClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final KakaoProperties kakaoProperties;

    public KakaoClient(
            ObjectMapper objectMapper,
            KakaoProperties kakaoProperties,
            @Value("${kakao.api.base-url:https://kapi.kakao.com}") String apiBaseUrl
    ) {
        this.objectMapper = objectMapper;
        this.kakaoProperties = kakaoProperties;
        this.restClient = RestClient.builder()
                .baseUrl(apiBaseUrl)
                .build();
    }

    public void sendOrderMessage(String kakaoAccessToken, Order order) {

        try{
        String messageText = buildMessageText(order);
        String templateJson;
        try {
            templateJson = objectMapper.writeValueAsString(
                    buildMessageTemplate(messageText)
            );
        } catch (Exception e) {
            throw new KakaoMessageSerializationException(e);
        }
        var body = new LinkedMultiValueMap<String, String>();
        body.add("template_object", templateJson);

        restClient.post()
                .uri("/v2/api/talk/memo/default/send")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    } catch (Exception e) {
        e.printStackTrace();
        throw new KakaoMessageSendException(e);
    }}

    private String buildMessageText(Order order) {
        Option option = order.getOption();
        Product product = option.getProduct();

        return String.format(
                "주문 완료! \n상품명: %s\n옵션: %s\n수량: %d개\n메세지: %s",
                product.getName(),
                option.getName(),
                order.getQuantity(),
                Optional.ofNullable(order.getMessage()).orElse("-")
        );
    }

    private Object buildMessageTemplate(String text) {
        return Map.of(
                "object_type", "text",
                "text", text,
                "link", Map.of(
                        "web_url", kakaoProperties.getLinkUrl(),
                        "mobile_web_url", kakaoProperties.getLinkUrl()
                ),
                "button_title", kakaoProperties.getButtonTitle()
        );
    }
}
