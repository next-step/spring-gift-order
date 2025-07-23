package gift.dto.response;

import gift.entity.Wish;

public class WishResponseDto {

    private final Long id;
    private final String productName;
    private final Integer quantity;

    public WishResponseDto(Wish wish) {
        this.id = wish.getId();
        this.productName = wish.getProduct().getName();
        this.quantity = wish.getQuantity();
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
