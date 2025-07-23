package gift.dto.product;

import gift.domain.product.Product;

import java.util.List;

public record ProductManageResponse(Long id, String name, String imageUrl, List<ProductOptionResponse> options) {

    public static ProductManageResponse from(Product product) {
        return new ProductManageResponse(product.getId(), product.getName(), product.getImageUrl(), product.getOptions().stream().map(ProductOptionResponse::from).toList());
    }
}
