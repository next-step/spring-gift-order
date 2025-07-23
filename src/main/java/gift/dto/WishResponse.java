package gift.dto;

public class WishResponse {

    private final ProductResponse product;
    private final Integer quantity;

    public WishResponse(ProductResponse product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
