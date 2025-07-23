package gift.dto;

import gift.entity.Product;
import java.time.LocalDateTime;

public class WishWithProductDto {
    private final Long wishId;
    private final Long memberId;
    private final Long productId;
    private final LocalDateTime createdAt;
    private final Product product;

    public WishWithProductDto(Long wishId, Long memberId, Long productId,
                              LocalDateTime createdAt, Product product) {
        this.wishId = wishId;
        this.memberId = memberId;
        this.productId = productId;
        this.createdAt = createdAt;
        this.product = product;
    }

    public Long getWishId() {
        return wishId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Product getProduct() {
        return product;
    }
}