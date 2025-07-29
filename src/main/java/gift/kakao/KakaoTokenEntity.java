package gift.kakao;

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

}
