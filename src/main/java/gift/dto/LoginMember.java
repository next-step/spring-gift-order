package gift.dto;

import jakarta.validation.constraints.NotNull;

public class LoginMember {

    private final Long id;

    @NotNull
    private final String email;

    public LoginMember(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
