package gift.dto.kakaoDto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoMessageDto(@JsonProperty("object_type") String objectType, String text, Link link, @JsonProperty("button_title") String buttonTitle) {

    public KakaoMessageDto(String text) {
        this("text", text, new Link("https://developers.kakao.com", "https://developers.kakao.com"), "확인");
    }

    public record Link(@JsonProperty("web_url") String webUrl, @JsonProperty("mobile_web_url") String mobileWebUrl) {
    }
}
