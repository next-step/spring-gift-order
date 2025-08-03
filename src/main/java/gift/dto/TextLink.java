package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TextLink {
    @JsonProperty("web_url")
    private String webUrl;

    @JsonProperty("mobile_web_url")
    private String mobileWebUrl;

    public TextLink(String url) {
        this.webUrl = url;
        this.mobileWebUrl = url;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getMobileWebUrl() {
        return mobileWebUrl;
    }
}