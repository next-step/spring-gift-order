package gift.dto;

import gift.entity.ProductOption;

public class ProductOptionResponse {
    private Long id;
    private String name;
    private int quantity;

    public ProductOptionResponse() {}

    public ProductOptionResponse(Long id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public ProductOptionResponse(ProductOption productOption) {
        this.id = productOption.getId();
        this.name = productOption.getName();
        this.quantity = productOption.getQuantity();
    }

    public static ProductOptionResponse from(ProductOption option) {
        return new ProductOptionResponse(
                option.getId(),
                option.getName(),
                option.getQuantity()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
