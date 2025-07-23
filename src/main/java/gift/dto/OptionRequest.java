package gift.dto;

public class OptionRequest {

    private final String name;
    private final Integer quantity;

    private OptionRequest(String name, Integer quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
