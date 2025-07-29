package gift.entity.Member;

import gift.dto.auth.AuthUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false, unique = true)
    private Long providerId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "access_token", length = 1000)
    private String accessToken;

    @Column(name = "refresh_token", length = 1000)
    private String refreshToken;

    protected Member() {
    }

    public Member(Long id, Long providerId, String email, String nickname, String profileImage,
        String accessToken, String refreshToken) {
        this.id = id;
        this.providerId = providerId;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Member(Long id, Long providerId, String email, String nickname, String profileImage) {
        this(id, providerId, email, nickname, profileImage, null, null);
    }

    public Member(Long providerId, String email, String nickname, String profileImage) {
        this(null, providerId, email, nickname, profileImage);
    }

    public Member(Long providerId, String email, String nickname, String profileImage,
        String accessToken, String refreshToken) {
        this(null, providerId, email, nickname, profileImage, accessToken, refreshToken);
    }

    public static Member from(AuthUser user) {
        return new Member(user.providerId(), user.email(), user.nickname(), user.profileImage(),
            user.accessToken(), user.refreshToken());
    }

    public void updateTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Long getId() {
        return id;
    }

    public Long getProviderId() {
        return providerId;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
