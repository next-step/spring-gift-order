package gift.dto;

import gift.domain.Wish;

public class WishResponse {
    private final Long id;
    private final Long optionId;
    private final String optionName;
    private final int quantity;
    private final Long productId;
    private final String productName;
    private final int productPrice;
    private final String productImageUrl;

    public WishResponse(Long id, Long optionId, String optionName, int quantity,
                        Long productId, String productName, int productPrice, String productImageUrl) {
        this.id = id;
        this.optionId = optionId;
        this.optionName = optionName;
        this.quantity = quantity;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
    }

    public static WishResponse from(Wish wish) {
        return new WishResponse(
                wish.getId(),
                wish.getProductOption().getId(),
                wish.getProductOption().getName(),
                wish.getQuantity(),
                wish.getProductOption().getProduct().getId(),
                wish.getProductOption().getProduct().getName(),
                wish.getProductOption().getProduct().getPrice(),
                wish.getProductOption().getProduct().getImageUrl()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getOptionId() {
        return optionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public int getQuantity() {
        return quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }
}




