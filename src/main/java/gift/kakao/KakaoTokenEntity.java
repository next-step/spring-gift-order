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
}
