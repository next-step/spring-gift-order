package gift.dto.product;

import gift.entity.Product;

public record ProductResponseDto(Long id, String name, int price, int quantity, String imageUrl) {

    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getQuantity(),
            product.getImageUrl()
        );
    }
}
