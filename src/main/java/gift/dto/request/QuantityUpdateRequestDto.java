package gift.dto.request;

public class QuantityUpdateRequestDto {
    private int quantity;

    public QuantityUpdateRequestDto(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
