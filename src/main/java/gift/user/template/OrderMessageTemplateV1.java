package gift.user.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.user.exception.KakaoSendMessageException;

import java.util.Map;

public class OrderMessageTemplateV1 implements MessageTemplate{

    private final String productName;
    private final String optionName;
    private final Long quantity;
    private final String message;

    public OrderMessageTemplateV1(String productName, String optionName, Long quantity, String message) {
        this.productName = productName;
        this.optionName = optionName;
        this.quantity = quantity;
        this.message = message;
    }

    @Override
    public String create() {
        Map<String, Object> template = Map.of(
                "object_type", "text",
                "text", String.format("주문 완료 🎉\n\n상품명: %s\n옵션: %s\n수량: %d개\n\n%s", productName, optionName, quantity, message),
                "link", Map.of("web_url", "https://your-site.com", "mobile_web_url", "https://your-site.com"),
                "button_title", "선물하러 가기"
        );

        try {
            return new ObjectMapper().writeValueAsString(template);
        } catch (Exception e) {
            throw new KakaoSendMessageException("메시지 템플릿 생성 실패");
        }
    }
}
