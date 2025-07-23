package gift.auth;

public class AuthenticationResult {
    private final boolean success;
    private final String errorMessage;
    private final Long memberId;
    private final String email;
    private final String role;

    private AuthenticationResult(boolean success, String errorMessage, Long memberId, String email, String role) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.memberId = memberId;
        this.email = email;
        this.role = role;
    }

    public static AuthenticationResult success(Long memberId, String email, String role) {
        return new AuthenticationResult(true, null, memberId, email, role);
    }

    public static AuthenticationResult failure(String errorMessage) {
        return new AuthenticationResult(false, errorMessage, null, null, null);
    }

    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    public Long getMemberId() { return memberId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
