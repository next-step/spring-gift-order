package gift.dto;

public class LoginMemberResponse {
    private final Long id;

    public LoginMemberResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
