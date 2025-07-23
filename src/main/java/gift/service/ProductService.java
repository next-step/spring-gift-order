package gift.service;

import gift.dto.request.ProductRequestDto;
import gift.dto.request.ProductUpdateRequestDto;
import gift.dto.response.ProductResponseDto;
import gift.entity.Product;
import java.util.List;

public interface ProductService {

    Product getProduct(long productId);

    void productExist(long productId);

    ProductResponseDto productToResponseDto(Product product);

    ProductResponseDto createProduct(ProductRequestDto productRequestDto);

    ProductResponseDto updateProduct(long productId,
        ProductUpdateRequestDto productUpdateRequestDto);

    void deleteProduct(long productId);

    List<Product> getAllProducts(int pageNo, String sortBy);

    Product findByName(String name);
}
