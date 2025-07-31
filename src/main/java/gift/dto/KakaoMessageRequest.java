package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoMessageRequest(
        @JsonProperty("template_object")
        TemplateObject templateObject
) {
    public record TemplateObject(
            @JsonProperty("object_type")
            String objectType,
            String text,
            Link link,
            @JsonProperty("button_title")
            String buttonTitle
    ) {}

    public record Link(
            @JsonProperty("web_url")
            String webUrl,
            @JsonProperty("mobile_web_url") 
            String mobileWebUrl
    ) {}

    public static KakaoMessageRequest createOrderMessage(String productName, Integer quantity, String message) {
        String text = String.format(
            "🎁 주문이 완료되었습니다!\n\n" +
            "상품명: %s\n" +
            "수량: %d개\n" +
            "메시지: %s\n\n" +
            "소중한 주문 감사합니다! 💝",
            productName, quantity, message != null ? message : "없음"
        );

        Link link = new Link("http://localhost:8080", "http://localhost:8080");
        TemplateObject templateObject = new TemplateObject("text", text, link, "주문 확인");
        
        return new KakaoMessageRequest(templateObject);
    }
}
