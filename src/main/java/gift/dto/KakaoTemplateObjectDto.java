package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoTemplateObjectDto {

    @JsonProperty("object_type")
    private String objectType = "text";

    @JsonProperty("text")
    private String text;

    @JsonProperty("link")
    private Links link;

    @JsonProperty("button_title")
    private String buttonTitle = "spring gift로";

    public KakaoTemplateObjectDto(String message, String serverUrl) {
        text = message;
        link = new Links(serverUrl);
    }

    public static class Links {
        @JsonProperty("web_url")
        private String webUrl;

        @JsonProperty("mobile_web_url")
        private String mobileWebUrl;

        public Links(String serverUrl) {
            webUrl = serverUrl;
            mobileWebUrl = serverUrl;
        }
    }
}
