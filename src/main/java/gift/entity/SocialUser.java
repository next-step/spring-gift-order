package gift.entity;

import jakarta.persistence.*;

@Entity
public class SocialUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String kakaoAccessToken;

    @Column(nullable = false, unique = true, length = 255)
    private String userEmail;

    protected SocialUser() {
    }

    public SocialUser(String userEmail, String kakaoAccessToken) {
        this.userEmail = userEmail;
        this.kakaoAccessToken = kakaoAccessToken;
    }

    public Long getId() {
        return id;
    }

    public String getKakaoAccessToken() {
        return kakaoAccessToken;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void updateToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
    }
}
