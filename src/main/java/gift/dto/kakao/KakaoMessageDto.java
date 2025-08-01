package gift.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.entity.Option;
import gift.entity.Order;

public record KakaoMessageDto(
        @JsonProperty("object_type") String objectType,
        String text,
        Link link,
        @JsonProperty("button_title") String buttonTitle
) {
    public record Link(
            @JsonProperty("web_url") String webUrl,
            @JsonProperty("mobile_web_url") String mobileWebUrl
    ) {
    }

    public static String toTemplateObject(Order order, Option option, ObjectMapper objectMapper) throws JsonProcessingException {
        String messageText = String.format(
                "주문이 완료되었습니다.\n\n- 상품명: %s\n- 옵션: %s\n- 수량: %d개\n- 메시지: %s",
                option.getProduct().getName(),
                option.getName(),
                order.getQuantity(),
                order.getMessage()
        );
        String productUrl = "http://localhost:8080/products/" + option.getProduct().getId();

        var messageDto = new KakaoMessageDto(
                "text",
                messageText,
                new Link(productUrl, productUrl),
                "주문 상세 보기"
        );
        return objectMapper.writeValueAsString(messageDto);
    }
}
