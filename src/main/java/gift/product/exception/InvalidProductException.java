package gift.product.exception;

public class InvalidProductException extends RuntimeException {

    private String field;

    public InvalidProductException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
