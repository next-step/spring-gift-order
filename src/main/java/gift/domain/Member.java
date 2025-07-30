package gift.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length = 320)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(name = "login_type")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name = "social_id", nullable = true)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public Member(){
    }

    public Member(Long id, String email, String password, LoginType loginType, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.loginType = loginType;
        this.role = role;
    }

    public Member(String email, String password, LoginType loginType, String socialId, Role role) {
        this.email = email;
        this.password = password;
        this.loginType = loginType;
        this.socialId = socialId;
        this.role = role;
    }

    public Member(Long id) {
        this.id = id;
    }

    public Member(String email, String password) {
        this.email = email;
        this.password = password;
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

    public LoginType getLoginType() {
        return loginType;
    }

    public String getSocialId() {
        return socialId;
    }

    public Role getRole() {
        return role;
    }

    @Column(name = "access_token", length = 2000)
    private String accessToken;

    public static Member createLocalMember(String email, String password) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        member.loginType = LoginType.LOCAL;
        member.role = Role.USER;
        return member;
    }

    public static Member createKakaoMember(String kakaoId) {
        return new Member(
                null,
                null,
                LoginType.KAKAO,
                kakaoId,
                Role.USER
        );
    }

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
