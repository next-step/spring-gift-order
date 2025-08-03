package gift.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "social_id")
    private Long socialId;

    @Column(name = "provider_type")
    private String providerType;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private MemberToken socialToken;

    public Member() {
    }

    public Member(String email, String password) {
        this(null, email, password, -1L, null);
    }

    public Member(String email, String password, Long socialId, String providerType) {
        this(null, email, password, socialId, providerType);
    }

    public Member(Long id, String email, String password, Long socialId, String providerType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.socialId = socialId;
        this.providerType = providerType;
    }

    public void updateSocialInfo(Long socialId, String providerType) {
        this.socialId = socialId;
        this.providerType = providerType;
    }

    public void addSocialToken(MemberToken token) {
        this.socialToken = token;
        token.setMember(this);
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

    public Long getSocialId() {
        return socialId;
    }

    public String getProviderType() {
        return providerType;
    }

    public MemberToken getSocialToken() {
        return socialToken;
    }
}
