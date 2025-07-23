package gift.service;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> findAllProducts();
    Page<ProductResponseDto> findAllProducts(Pageable pageable);
    ProductResponseDto saveProduct(ProductRequestDto dto);
    ProductResponseDto findProductById(Long id);
    ProductResponseDto updateProduct(Long id, ProductRequestDto dto);
    void deleteProduct(Long id);
}
