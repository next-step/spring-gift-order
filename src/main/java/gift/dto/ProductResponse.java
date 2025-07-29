package gift.dto;

import gift.domain.ProductOption;
import java.util.List;

public class ProductResponse {
    private Long id;
    private String name;
    private int price;
    private String imageUrl;
    private List<ProductOptionResponse> options;

    public ProductResponse(Long id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
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

    public List<ProductOptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<ProductOptionResponse> options) {
        this.options = options;
    }
}


