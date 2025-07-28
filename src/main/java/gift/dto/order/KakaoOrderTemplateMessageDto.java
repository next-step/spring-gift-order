package gift.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoOrderTemplateMessageDto(
    @JsonProperty("object_type") String objectType,
    @JsonProperty("content") KakaoOrderTemplateMessageContentDto content
) {
    public record KakaoOrderTemplateMessageContentDto(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("link") KakaoOrderTemplateMessageLinkDto link
    ) {}

    public record KakaoOrderTemplateMessageLinkDto(
        @JsonProperty("web_url") String webUrl,
        @JsonProperty("mobile_web_url") String mobileWebUrl
    ) {}
}
