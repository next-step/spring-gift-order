package gift.dto;

public class WishDto {
    private final String productName;
    private final String optionName;
    private final int quantity;

    public WishDto(String productName, String optionName, int quantity) {
        this.productName = productName;
        this.optionName = optionName;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public String getOptionName() {
        return optionName;
    }

    public int getQuantity() {
        return quantity;
    }
}
