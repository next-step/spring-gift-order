package gift.service;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.entity.Product;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 1. 상품 등록
    @Override
    public ProductResponseDto create(ProductRequestDto dto) {

        if (dto.getName().contains("카카오")) {
            throw new IllegalArgumentException("상품명에 '카카오'를 포함하려면 담당 MD와의 협의가 필요합니다.");
        }

        Product product = Product.of(dto.getName(), dto.getImageUrl(), dto.getPrice());
        Product saved = productRepository.save(product);

        return new ProductResponseDto(saved);
    }

    // 2-1. 전체 상품 조회
    @Override
    public Page<ProductResponseDto> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductResponseDto::new);
    }

    // 2-2. 특정 상품 조회
    @Override
    public ProductResponseDto findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return new ProductResponseDto(product);
    }

    // 3. 상품 수정
    @Override
    public ProductResponseDto update(Long id, ProductRequestDto dto) {

        if (dto.getName().contains("카카오")) {
            throw new IllegalArgumentException("상품명에 '카카오'를 포함하려면 담당 MD와의 협의가 필요합니다.");
        }

        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NO_CONTENT));

        product.update(dto.getName(), dto.getImageUrl(), dto.getPrice());
        productRepository.save(product);
        return new ProductResponseDto(product);
    }

    // 4. 상품 삭제
    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NO_CONTENT));

        productRepository.deleteById(id);
    }
}
