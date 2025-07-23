package gift.dto;

import java.time.LocalDateTime;

public class WishResponseDto {
    private Long wishId;
    private Long productId;
    private String productName;
    private Long price;
    private String productImageUrl;
    private LocalDateTime createdAt;

    public WishResponseDto(Long wishId, Long productId, String productName, Long Price, String productImageUrl, LocalDateTime createdAt) {
        this.wishId = wishId;
        this.productId = productId;
        this.productName = productName;
        this.price = Price;
        this.productImageUrl = productImageUrl;
        this.createdAt = createdAt;
    }

    public Long getWishId() { return wishId; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Long getPrice() { return price; }
    public String getProductImageUrl() { return productImageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
