package gift.product.exception;

public class InvalidProductOptionException extends RuntimeException {

    private String field;

    public InvalidProductOptionException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
