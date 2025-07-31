package gift.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {
    private static String clientId;
    private static String redirectUri;

    public static String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public static String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
}
