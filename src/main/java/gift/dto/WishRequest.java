package gift.dto;

public class WishRequest {
    private Long productId;
    private int quantity;

    public WishRequest() {
    }

    public WishRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
