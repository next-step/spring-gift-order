package gift.entity;

import gift.domain.member.Email;
import gift.domain.member.Password;
import gift.domain.member.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String kakaoId;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "kakao_access_token")
    private String kakaoAccessToken;

    @Column(name = "kakao_refresh_token")
    private String kakaoRefreshToken;

    @Column(name = "kakao_token_expiry")
    private LocalDateTime kakaoTokenExpiry;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wish> wishes = new ArrayList<>();

    protected Member() {}

    public Member(Long id, Email email, Password password, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member(Email email, Password password, Role role) {
        this.id = null;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member(Email email, Password password) {
        this.id = null;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
    }

    public Member(String kakaoId, Role role) {
        this.kakaoId = kakaoId;
        this.role = role;
    }

    public static Member fromKakao(String kakaoId) {
        return new Member(kakaoId, Role.USER);
    }

    public Long getId() { return id; }
    public Email getEmail() { return email; }
    public Password getPassword() { return password; }
    public Role getRole() { return role; }

    public void setKakaoAccessToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
    }

    public void setKakaoRefreshToken(String kakaoRefreshToken) {
        this.kakaoRefreshToken = kakaoRefreshToken;
    }

    public void setKakaoTokenExpiry(LocalDateTime kakaoTokenExpiry) {
        this.kakaoTokenExpiry = kakaoTokenExpiry;
    }

    public String getKakaoId() {
        return kakaoId;
    }

    public String getKakaoAccessToken() {
        return kakaoAccessToken;
    }

    public void setEmail(String s) {
        this.email = new Email(s);
    }
}