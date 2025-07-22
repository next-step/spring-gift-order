package gift.service.product;

import gift.dto.product.ProductRequestDto;
import gift.dto.product.ProductResponseDto;
import org.springframework.data.domain.Page;

public interface ProductService {

    Page<ProductResponseDto> findAll(int page, int size);

    ProductResponseDto create(ProductRequestDto requestDto);

    ProductResponseDto findById(Long id);

    ProductResponseDto update(Long id, ProductRequestDto requestDto);

    void delete(Long id);
}
