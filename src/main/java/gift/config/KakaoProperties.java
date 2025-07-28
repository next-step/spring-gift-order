package gift.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {
    private String clientId;
    private String redirectUri;
    private String oauthTokenUrlHost;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getOauthTokenUrlHost() {
        return oauthTokenUrlHost;
    }

    public void setOauthTokenUrlHost(String oauthTokenUrlHost) {
        this.oauthTokenUrlHost = oauthTokenUrlHost;
    }

    public String buildAuthorizationUrl() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" 
               + clientId + "&redirect_uri=" + redirectUri;
    }
}
