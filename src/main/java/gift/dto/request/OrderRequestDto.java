package gift.dto.request;

public class OrderRequestDto {
    private Long optionId;
    private int quantity;
    private String message;

    public Long getOptionId() {
        return optionId;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMessage() {
        return message;
    }

}
