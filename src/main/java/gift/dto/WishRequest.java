package gift.dto;

public class WishRequest {
    private Long optionId;
    private int quantity;

    public WishRequest() {}

    public WishRequest(Long optionId, int quantity) {
        this.optionId = optionId;
        this.quantity = quantity;
    }

    public Long getOptionId() {
        return optionId;
    }

    public int getQuantity() {
        return quantity;
    }
}


