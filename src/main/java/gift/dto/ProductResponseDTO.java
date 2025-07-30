package gift.dto;

import gift.entity.Product;

public record ProductResponseDTO(
    Long id,
    String name,
    Long price,
    String imageUrl
) {
    public ProductResponseDTO(Product product) {
        this(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getImageUrl()
        );
    }
}
