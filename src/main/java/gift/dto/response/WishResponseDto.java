package gift.dto.response;

import gift.entity.Wish;

public class WishResponseDto {
    private Long wishId;
    private Long productId;
    private String name;
    private int price;
    private String imageUrl;
    private int quantity;

    public WishResponseDto(Long wishId, Long productId, String name, int price, String imageUrl, int quantity) {
        this.wishId = wishId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public WishResponseDto(Wish wish) {
        this.wishId = wish.getId();
        this.productId = wish.getProduct().getId();
        this.name = wish.getProduct().getName();
        this.price = wish.getProduct().getPrice();
        this.imageUrl = wish.getProduct().getImageUrl();
        this.quantity = wish.getQuantity();
    }

    public Long getWishId() {
        return wishId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setWishId(Long wishId) {
        this.wishId = wishId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
