package gift.dto;

import gift.entity.Product;
import java.util.List;
import java.util.stream.Collectors;

public record ProductResponse(
    Long id,
    String name,
    Integer price,
    String imageUrl,
    List<ProductOptionResponse> options
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getImageUrl(),
            product.getOptions().stream()
                .map(ProductOptionResponse::from)
                .collect(Collectors.toList())
        );
    }
}
