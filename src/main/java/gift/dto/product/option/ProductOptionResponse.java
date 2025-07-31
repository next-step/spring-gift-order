package gift.dto.product.option;

import gift.entity.product.option.ProductOption;

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
