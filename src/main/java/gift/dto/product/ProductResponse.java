package gift.dto.product;

import gift.domain.product.Product;

import java.util.List;

public record ProductResponse(Long id, String name, String imageUrl, List<ProductOptionResponse> options) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getImageUrl(), product.getOptions().stream().map(ProductOptionResponse::from).toList());
    }

}
