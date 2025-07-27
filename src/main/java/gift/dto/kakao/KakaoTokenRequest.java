package gift.dto.kakao;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoTokenRequest {

    private final String grant_type = "authorization_code"; // 값 고정
    private String client_id;
    private String redirect_uri;
    private String code;

    public static class Builder {
        private String clientId;
        private String redirectUri;
        private String code;

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public KakaoTokenRequest build() {
            KakaoTokenRequest dto = new KakaoTokenRequest();
            dto.client_id = this.clientId;
            dto.redirect_uri = this.redirectUri;
            dto.code = this.code;
            return dto;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", grant_type);
        map.add("client_id", client_id);
        map.add("redirect_uri", redirect_uri);
        map.add("code", code);
        return map;
    }
}