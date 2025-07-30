package gift.event;

public class OrderEvent {
    private final String userEmail;
    private final String message;

    public OrderEvent(String userEmail, String message) {
        this.userEmail = userEmail;
        this.message = message;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getMessage() {
        return message;
    }
}
