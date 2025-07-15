package gift.member.exception;

public class InvalidMemberException extends RuntimeException {
    private String field;
    private InvalidMemberException invalidMemberException;

    public InvalidMemberException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return this.field;
    }
}
