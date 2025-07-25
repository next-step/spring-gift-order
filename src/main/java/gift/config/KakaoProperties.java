package gift.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {
    private String clientId;
    private String redirectUri;
    private String loginUrl;

    @PostConstruct
    public void init() {
        this.loginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                + clientId + "&redirect_uri=" + redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
