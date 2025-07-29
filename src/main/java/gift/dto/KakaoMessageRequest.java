package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoMessageRequest {

    @JsonProperty("template_object")
    private final TemplateObject templateObject;

    private KakaoMessageRequest(TemplateObject templateObject) {
        this.templateObject = templateObject;
    }

    public static KakaoMessageRequest from(String text, String webUrl) {
        var link = new Link(webUrl, webUrl);
        var templateObject = new TemplateObject("text", text, link);
        return new KakaoMessageRequest(templateObject);
    }

    public TemplateObject getTemplateObject() {
        return templateObject;
    }

    private static class TemplateObject {
        @JsonProperty("object_type")
        private final String objectType;
        private final String text;
        private final Link link;

        public TemplateObject(String objectType, String text, Link link) {
            this.objectType = objectType;
            this.text = text;
            this.link = link;
        }

        public String getObjectType() {
            return objectType;
        }

        public String getText() {
            return text;
        }

        public Link getLink() {
            return link;
        }
    }

    private static class Link {
        @JsonProperty("web_url")
        private final String webUrl;
        @JsonProperty("mobile_web_url")
        private final String mobileWebUrl;

        public Link(String webUrl, String mobileWebUrl) {
            this.webUrl = webUrl;
            this.mobileWebUrl = mobileWebUrl;
        }

        public String getWebUrl() {
            return webUrl;
        }

        public String getMobileWebUrl() {
            return mobileWebUrl;
        }
    }
}
