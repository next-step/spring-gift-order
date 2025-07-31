package gift.service.product;

import gift.dto.pagination.PageResponse;
import gift.dto.pagination.Pagination;
import gift.dto.product.ProductRequest;
import gift.dto.product.ProductResponse;
import gift.dto.product.ProductUpdateRequest;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    PageResponse<ProductResponse> getAllProducts(Pagination pagination);

    ProductResponse getProduct(Long productId);

    ProductResponse update(Long productId, ProductUpdateRequest request);

    void delete(Long productId);
}
