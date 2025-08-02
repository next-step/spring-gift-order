package gift.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kakao")
public class Properties {

    private String restApiKey;
    private String authUrl;
    private String apiUrl;
    private String redirectUri;
    private String corsAllowedOrigin;

    public Properties() {
    }

    public Properties(String restApiKey, String authUrl, String apiUrl, String redirectUri, String corsAllowedOrigin) {
        this.restApiKey = restApiKey;
        this.authUrl = authUrl;
        this.apiUrl = apiUrl;
        this.redirectUri = redirectUri;
        this.corsAllowedOrigin = corsAllowedOrigin;
    }

    public String getRestApiKey() {
        return restApiKey;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getCorsAllowedOrigin() {
        return corsAllowedOrigin;
    }

    public void setRestApiKey(String restApiKey) {
        this.restApiKey = restApiKey;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setCorsAllowedOrigin(String corsAllowedOrigin) {
        this.corsAllowedOrigin = corsAllowedOrigin;
    }
}
