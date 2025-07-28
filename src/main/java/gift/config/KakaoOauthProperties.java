package gift.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "kakao")
public record KakaoOauthProperties(
        String clientId,
        String clientSecret,
        String redirectUri
) {

    @ConstructorBinding
    public KakaoOauthProperties(String clientId, String clientSecret, String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }
}
