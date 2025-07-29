package gift.domain;

public class ProductOptionRequest {
    private String name;
    private int quantity;

    protected ProductOptionRequest() {}

    public ProductOptionRequest(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
