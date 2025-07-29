package gift.service.Product;

import gift.dto.Pagination.PageResponse;
import gift.dto.Pagination.Pagination;
import gift.dto.Product.ProductRequest;
import gift.dto.Product.ProductResponse;
import gift.dto.Product.ProductUpdateRequest;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    PageResponse<ProductResponse> getAllProducts(Pagination pagination);

    ProductResponse getProduct(Long productId);

    ProductResponse update(Long productId, ProductUpdateRequest request);

    void delete(Long productId);
}
