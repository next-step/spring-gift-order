package gift.builder;

import gift.dto.request.ProductRequestDto;
import gift.entity.Product;

public class ProductBuilder {
    private String name = "기본상품명";
    private int price = 1000;
    private String imageUrl = "http://default.image.url";

    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }


    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductRequestDto build() {
        return new ProductRequestDto(name, price, imageUrl);
    }

    public Product buildEntity() {
        return new Product(null, name, price, imageUrl);
    }
}
