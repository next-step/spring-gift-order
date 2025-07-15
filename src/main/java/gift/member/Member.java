package gift.member;

import gift.member.dto.MemberResponseDto;
import gift.member.dto.MemberUpdateRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String name;
    @Enumerated(EnumType.STRING)
    private Role role;

    public MemberResponseDto toMemberResponseDto() {
        return new MemberResponseDto(this);
    }

    protected Member(){}

    public Member(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
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
}
