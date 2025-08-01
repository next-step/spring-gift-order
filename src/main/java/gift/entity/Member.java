package gift.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "member",cascade = CascadeType.PERSIST,orphanRemoval = true)
    private List<Wish> wishes = new ArrayList<>();

    private String nickname;
    private String profileImageUrl;

    @Column(length = 512)
    private String kakaoAccessToken;

    protected Member() {
    }

    public Member(Long id, String email, String password, Role role, String nickname, String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public Member(String email, String password) {
        this(null, email, password, Role.USER, null, null);
    }

    public Member(Long id, String email, String password) {
        this(id, email, password, null, null, null);
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateKakaoAccessToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getKakaoAccessToken() {
        return kakaoAccessToken;
    }

    public List<Wish> getWishes() {
        return wishes;
    }
}