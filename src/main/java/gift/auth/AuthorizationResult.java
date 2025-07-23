package gift.auth;

public class AuthorizationResult {
    private final boolean success;
    private final String errorMessage;

    private AuthorizationResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static AuthorizationResult success() {
        return new AuthorizationResult(true, null);
    }

    public static AuthorizationResult failure(String errorMessage) {
        return new AuthorizationResult(false, errorMessage);
    }

    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}
