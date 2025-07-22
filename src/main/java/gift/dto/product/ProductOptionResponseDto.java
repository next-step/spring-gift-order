package gift.dto.product;

import gift.entity.ProductOption;

public record ProductOptionResponseDto(Long id, String name, int quantity) {

    public static ProductOptionResponseDto from(ProductOption option) {
        return new ProductOptionResponseDto(option.getId(), option.getName(), option.getQuantity());
    }
}
