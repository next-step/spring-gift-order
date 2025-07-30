package gift.kakao;

import gift.kakao.dto.KakaoTokenResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "kakao_token")
public class KakaoTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "access_token_expires_at", nullable = false)
    private LocalDateTime accessTokenExpiresAt;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "refresh_token_expires_at", nullable = false)
    private LocalDateTime refreshTokenExpiresAt;

    protected KakaoTokenEntity() {
    }

    public static KakaoTokenEntity from(KakaoTokenResponseDto dto) {
        KakaoTokenEntity entity = new KakaoTokenEntity();
        LocalDateTime now = LocalDateTime.now();
        entity.setAccessToken(dto.accessToken());
        entity.setAccessTokenExpiresAt(now.plusSeconds(dto.expiresIn()));
        entity.setRefreshToken(dto.refreshToken());
        entity.setRefreshTokenExpiresAt(now.plusSeconds(dto.refreshTokenExpiresIn()));
        return entity;
    }

    public KakaoTokenEntity updateAccessToken(String accessToken, long accessTokenExpiresIn) {
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = LocalDateTime.now().plusSeconds(accessTokenExpiresIn);
        return this;
    }

    public KakaoTokenEntity updateRefreshToken(String refreshToken, long refreshTokenExpiresIn) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiresIn);
        return this;
    }

    public boolean isAccessTokenExpired() {
        // 만료시간 체크와 api 호출 사이에 만료되는 경우를 고려하여 버퍼를 둠.
        return LocalDateTime.now().isAfter(accessTokenExpiresAt.minusSeconds(10));
    }

    public boolean isRefreshTokenExpired() {
        return LocalDateTime.now().isAfter(refreshTokenExpiresAt.minusSeconds(10));
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessTokenExpiresAt(LocalDateTime accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setRefreshTokenExpiresAt(LocalDateTime refreshTokenExpiresAt) {
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
