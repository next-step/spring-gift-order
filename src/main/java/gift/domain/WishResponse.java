package gift.domain;

public class WishResponse {
    private Long id;
    private Long productId;
    private String productName;
    private int productPrice;
    private String productImageUrl;

    public WishResponse(Wish wish) {
        this.id = wish.getId();
        this.productId = wish.getProduct().getId();
        this.productName = wish.getProduct().getName();
        this.productPrice = wish.getProduct().getPrice();
        this.productImageUrl = wish.getProduct().getImageUrl();
    }

    public Long getId() {
        return id;
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

