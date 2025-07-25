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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(nullable = true)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public Member(){
    }

    public Member(Long id) {
        this.id = id;
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

    public static Member createLocalMember(String email, String password) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        member.authProvider = AuthProvider.LOCAL;
        member.role = Role.USER;
        return member;
    }

    public static Member createKakaoMember(String providerId) {
        Member member = new Member();
        member.authProvider = AuthProvider.KAKAO;
        member.providerId = providerId;
        member.role = Role.USER;
        return member;
    }

}
