package gift.service;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    // 1. 상품 등록
    public ProductResponseDto create(ProductRequestDto dto);

    // 2-1. 전체 상품 조회
    public Page<ProductResponseDto> findAll(Pageable pageable);

    // 2-2. 특정 상품 조회
    public ProductResponseDto findById(Long id);

    // 3. 상품 수정
    public ProductResponseDto update(Long id, ProductRequestDto dto);

    // 4. 상품 삭제
    public void delete(Long id);

}
