package gift.dto;

public class OrderRequest {
    private Long optionId;
    private int quantity;
    private String message;

    public OrderRequest(Long optionId, int quantity, String message) {
        this.optionId = optionId;
        this.quantity = quantity;
        this.message = message;
    }

    public Long getOptionId() {
        return optionId;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getMessage() {
        return message;
    }

}
