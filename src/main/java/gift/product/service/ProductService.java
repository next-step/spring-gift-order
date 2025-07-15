package gift.product.service;

import gift.product.Product;
import gift.product.dto.ProductAddRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.dto.ProductUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    void addProduct(ProductAddRequestDto requestDto);
    ProductResponseDto findProductById(Long id);
    List<ProductResponseDto> findAllProducts();

    Page<ProductResponseDto> findAllProductsWithPageable(Pageable pageable);

    void updateProductById(Long id, ProductUpdateRequestDto requestDto);
    void deleteProductById(Long id);

    Product findProductByIdOrElseThrow(Long id);
}
