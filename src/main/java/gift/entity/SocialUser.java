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
    private String kakaoEmail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    protected SocialUser() {}

    public SocialUser(String kakaoEmail, String kakaoAccessToken) {
        this.kakaoEmail = kakaoEmail;
        this.kakaoAccessToken = kakaoAccessToken;
    }

    public void AddUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getKakaoAccessToken() {
        return kakaoAccessToken;
    }

    public String getKakaoEmail() {
        return kakaoEmail;
    }

    public User getUser() {
        return user;
    }

    public void updateToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
    }
}
