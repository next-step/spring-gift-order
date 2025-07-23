package gift.dto;

import gift.entity.Product;
import gift.validation.ProductNameValid;

public class ProductRequest {

    @ProductNameValid
    private final String name;

    private final int price;
    private final String imageUrl;

    public ProductRequest(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
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

    public Product toEntity() {
        return new Product(name, price, imageUrl);
    }
}
