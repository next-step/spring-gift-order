package gift.dto.Product.Option;

import gift.entity.ProductOption;

public record ProductOptionResponse(
    Long id,
    String name,
    Long quantity
) {

    public static ProductOptionResponse from(ProductOption option) {
        return new ProductOptionResponse(
            option.getId(),
            option.getName(),
            option.getQuantity()
        );
    }
}
