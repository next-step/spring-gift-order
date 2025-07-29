package gift.authorization.oauth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class KakaoToken {

    @Id
    private String clientId; // 카카오 유저 ID
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;

    protected KakaoToken() {
    }

    public KakaoToken(String clientId, String accessToken, String refreshToken, LocalDateTime expiresAt) {
        this.clientId = clientId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void update(String newAccessToken, String newRefreshToken, LocalDateTime newExpiresAt) {
        this.accessToken = newAccessToken;
        this.refreshToken = newRefreshToken;
        this.expiresAt = newExpiresAt;
    }

    public String getClientId() {
        return clientId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
