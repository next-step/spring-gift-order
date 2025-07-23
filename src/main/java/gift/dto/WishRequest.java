package gift.dto;

public class WishRequest {

    private final Long productId;
    private final Integer quantity;

    public WishRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
