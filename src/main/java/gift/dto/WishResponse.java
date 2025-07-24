package gift.dto;

import gift.domain.Wish;

public class WishResponse {
    private Long id;
    private ProductResponse product;

    public WishResponse(Long id, ProductResponse product) {
        this.id = id;
        this.product = product;
    }

    public static WishResponse from(Wish wish) {
        return new WishResponse(wish.getId(), ProductResponse.from(wish.getProduct()));
    }

    public Long getId() {
        return id;
    }

    public ProductResponse getProduct() {
        return product;
    }
}
