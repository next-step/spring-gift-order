package gift.dto;

import gift.entity.Member;
import jakarta.validation.constraints.NotNull;

public class MemberDto {

    private final Long id;

    @NotNull
    private final String email;

    @NotNull
    private final String password;

    public MemberDto(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
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
}
