package gift.dto;

import gift.entity.Product;

import java.util.List;

public record ProductResponseDto(Long id, String name, Long price, String imageUrl, boolean mdApproved, List<ProductOptionResponse> options) {
    public ProductResponseDto(Product product) {
        this(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.isApproved(),
                product.getOptions().stream()
                .map(ProductOptionResponse::new)
                .toList());
    }
}