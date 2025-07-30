package gift.dto;

public class KakaoMessageDTO {
    private String object_type;
    private String text;
    private KakaoLinkDTO link;
    private String button_title;

    public KakaoMessageDTO() {
    }

    public KakaoMessageDTO(String text) {
        this.object_type = "text";
        this.text = text;
        this.link = new KakaoLinkDTO();
    }

    public String getObject_type() {
        return object_type;
    }

    public void setObject_type(String object_type) {
        this.object_type = object_type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public KakaoLinkDTO getLink() {
        return link;
    }

    public void setLink(KakaoLinkDTO link) {
        this.link = link;
    }

    public String getButton_title() {
        return button_title;
    }

    public void setButton_title(String button_title) {
        this.button_title = button_title;
    }

    public static class KakaoLinkDTO {
        private String web_url;
        private String mobile_web_url;

        public KakaoLinkDTO() {
            // 임시로 예제에 있는 링크 넣었습니다.
            this.web_url = "https://developers.kakao.com";
            this.mobile_web_url = "https://developers.kakao.com";
        }

        public String getWeb_url() {
            return web_url;
        }

        public void setWeb_url(String web_url) {
            this.web_url = web_url;
        }

        public String getMobile_web_url() {
            return mobile_web_url;
        }

        public void setMobile_web_url(String mobile_web_url) {
            this.mobile_web_url = mobile_web_url;
        }
    }
}
