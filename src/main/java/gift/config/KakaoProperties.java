package gift.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:secrets.properties") // resources 디렉토리 내 확인
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {

    private String clientId;
    private String redirectUri;
    private String clientSecret;

    public String clientId() {
        return clientId;
    }

    public String redirectUri() {
        return redirectUri;
    }

    public String clientSecret() {
        return clientSecret;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}