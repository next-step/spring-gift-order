package gift.member;

import gift.member.dto.MemberResponseDto;
import gift.member.dto.MemberUpdateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    @Column(nullable = true)
    private String password;
    private String name;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String clientId;
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    public MemberResponseDto toMemberResponseDto() {
        return new MemberResponseDto(this);
    }

    protected Member() {
    }

    public Member(String email, String password, String name, Role role, String clientId, AuthType authType) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.clientId = clientId;
        this.authType = authType;
    }

    public static Member createEmailMember(String email, String hashedPassword, String name) {
        return new Member(email, hashedPassword, name, Role.USER, "", AuthType.EMAIL);
    }

    public static Member createKakaoMember(String clientId) {
        return new Member("", "", "", Role.USER, clientId, AuthType.KAKAO);
    }

    public void update(MemberUpdateRequestDto requestDto) {
        this.email = requestDto.email();
        this.name = requestDto.name();
        this.role = requestDto.role();
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public String getClientId() {
        return clientId;
    }
}
