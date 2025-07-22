package gift.service;

import gift.dto.PageResponse;
import gift.dto.Pagination;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    PageResponse<ProductResponse> getAllProducts(Pagination pagination);

    ProductResponse getProduct(Long id);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
}
