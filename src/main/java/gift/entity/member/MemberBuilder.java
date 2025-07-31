package gift.entity.member;

public class MemberBuilder {

    private Long id;
    private Long providerId;
    private String email;
    private String nickname;
    private String profileImage;
    private String accessToken;
    private String refreshToken;

    private MemberBuilder() {
    }

    public static MemberBuilder builder() {
        return new MemberBuilder();
    }

    public MemberBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public MemberBuilder providerId(Long providerId) {
        this.providerId = providerId;
        return this;
    }

    public MemberBuilder email(String email) {
        this.email = email;
        return this;
    }

    public MemberBuilder nickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public MemberBuilder profileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public MemberBuilder accessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public MemberBuilder refreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public Member build() {
        return new Member(
            id,
            providerId,
            email,
            nickname,
            profileImage,
            accessToken,
            refreshToken
        );
    }
}
