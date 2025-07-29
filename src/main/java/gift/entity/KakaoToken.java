package gift.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kakao_token")
public class KakaoToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tokenType;
    private String accessToken;
    private String idToken;

    private LocalDateTime expiresDate; // 서비스 레이어에서 계산 필요

    private String refreshToken;
    private LocalDateTime refreshTokenExpiresDate; // 서비스 레이어에서 계산 필요
    private String scope;

    protected KakaoToken() {}

    public KakaoToken(String tokenType, String accessToken, String idToken, LocalDateTime expiresDate, String refreshToken, LocalDateTime refreshTokenExpiresDate, String scope) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.expiresDate = expiresDate;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresDate = refreshTokenExpiresDate;
        this.scope = scope;
    }

    public void updateAccessToken(String accessToken, LocalDateTime expiresDate){
        this.accessToken = accessToken;
        this.expiresDate = expiresDate;
    }

    public void updateRefreshToken(String refreshToken, LocalDateTime refreshTokenExpiresDate){
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresDate = refreshTokenExpiresDate;
    }

    public Long getId() {return id;}
    public String getTokenType() {return tokenType;}
    public String getAccessToken() {return accessToken;}
    public String getIdToken() {return idToken;}
    public LocalDateTime getExpiresDate() {return expiresDate;}
    public String getRefreshToken() {return refreshToken;}
    public LocalDateTime getRefreshTokenExpiresDate() {return refreshTokenExpiresDate;}
    public String getScope() {return scope;}
}
