package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoTextResponse {
    @JsonProperty("object_type")
    private final String objectType = "text";

    private String text;
    private Link link;

    @JsonProperty("button_title")
    private String buttonTitle;

    public KakaoTextResponse(String text, String webUrl, String mobileUrl, String buttonTitle) {
        this.text = text;
        this.link = new Link(webUrl, mobileUrl);
        this.buttonTitle = buttonTitle;
    }

    public static class Link {
        @JsonProperty("web_url")
        private String webUrl;

        @JsonProperty("mobile_web_url")
        private String mobileWebUrl;

        public Link(String webUrl, String mobileWebUrl) {
            this.webUrl = webUrl;
            this.mobileWebUrl = mobileWebUrl;
        }
    }
}
