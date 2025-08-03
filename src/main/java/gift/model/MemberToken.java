package gift.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "member_token")
public class MemberToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_token")
    private String accessToken;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    protected MemberToken() {
    }

    public MemberToken(String accessToken) {
        this(null, accessToken, null);
    }

    public MemberToken(Long id, String accessToken, Member member) {
        this.id = id;
        this.accessToken = accessToken;
        this.member = member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Member getMember() {
        return member;
    }

}
