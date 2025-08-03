package gift.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_kakao_token")
public class UserKakaoToken {

  @Id
  private Long userId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id")
  private Member member;

  @Column(nullable = false)
  private String accessToken;

  @Column(nullable = false)
  private String refreshToken;

  private Instant accessTokenExpiresAt;
  private Instant refreshTokenExpiresAt;

  protected UserKakaoToken() {}

  public UserKakaoToken(Member member, String accessToken, String refreshToken,
      Instant accessTokenExpiresAt, Instant refreshTokenExpiresAt) {
    this.member = member;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.accessTokenExpiresAt = accessTokenExpiresAt;
    this.refreshTokenExpiresAt = refreshTokenExpiresAt;
  }

  public void updateToken(String accessToken, String refreshToken, Instant accessTokenExpiresAt, Instant refreshTokenExpiresAt) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.accessTokenExpiresAt = accessTokenExpiresAt;
    this.refreshTokenExpiresAt = refreshTokenExpiresAt;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public Instant getAccessTokenExpiresAt() {
    return accessTokenExpiresAt;
  }

  public Member getMember(){
    return member;
  }

  public boolean isExpired() {
    return accessTokenExpiresAt != null && accessTokenExpiresAt.isBefore(Instant.now());
  }
}
