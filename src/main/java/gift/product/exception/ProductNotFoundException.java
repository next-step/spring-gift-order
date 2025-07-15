package gift.product.exception;

public class ProductNotFoundException extends RuntimeException {

    private final Long id;

    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
        this.id = id;
    }
}
