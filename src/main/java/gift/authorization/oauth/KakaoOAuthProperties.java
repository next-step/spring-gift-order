package gift.authorization.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@ConfigurationProperties(prefix = "kakao")
public class KakaoOAuthProperties {
    private String clientId;
    private String redirectUri;
    private String tokenUri;
    private String userInfoUri;

    public void setClientId(String clientId) { this.clientId = clientId; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
    public void setTokenUri(String tokenUri) { this.tokenUri = tokenUri; }
    public void setUserInfoUri(String userInfoUri) { this.userInfoUri = userInfoUri; }


    public String getKakaoLoginUrl() {
        return UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", this.clientId)
                .queryParam("redirect_uri", this.redirectUri)
                .build()
                .toUriString();
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }
}
