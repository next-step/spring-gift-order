package gift.product.dto;

import gift.product.Product;
import gift.product.ProductOption;

import java.util.List;

public record ProductResponseDto (Long id, String name, Long price, String url, List<ProductOption> options){
    public ProductResponseDto(Product product) {
        this(product.getId(), product.getName(), product.getPrice(), product.getUrl(), product.getOptions());
    }
}
