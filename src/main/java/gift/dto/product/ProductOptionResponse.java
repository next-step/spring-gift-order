package gift.dto.product;

import gift.domain.product.ProductOption;

public record ProductOptionResponse(Long id, String name, Integer price, Integer quantity) {

    public static ProductOptionResponse from(ProductOption option) {
        return new ProductOptionResponse(option.getId(), option.getName(), option.getPrice(), option.getQuantity());
    }
}
