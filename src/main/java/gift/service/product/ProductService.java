package gift.service.product;

import gift.domain.Product;
import gift.dto.product.ProductRequest;
import gift.dto.product.ProductResponse;
import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import gift.global.exception.InvalidRequestException;
import gift.repository.product.ProductJpaRepository;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private static final Set<String> ALLOWED_SORT_NAMES = Set.of(
        "id", "name", "price"
    );
    private final ProductJpaRepository productRepository;

    public ProductService(ProductJpaRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findOrThrow(productId);

        return ProductResponse.from(product);
    }

    // 페이지네이션: product 목록 조회
    public Page<ProductResponse> getProductPage(Pageable pageable) {
        validate(pageable);

        return productRepository.findAll(pageable)
            .map(ProductResponse::from);
    }

    public ProductResponse insert(ProductRequest request) {
        if (request.name().contains("카카오")) {
            throw InvalidRequestException.from(ErrorCode.INVALID_KAKAO_NAME);
        }

        return ProductResponse.from(productRepository.save(Product.from(request)));
    }

    @Transactional
    public void update(ProductRequest request) {
        // 이 경우에는 request.id()에 수정하고자 하는 상품id가 담겨서 넘어옵니다.
        productRepository.findOrThrow(request.id());

        if (request.name().contains("카카오")) {
            throw InvalidRequestException.from(ErrorCode.INVALID_KAKAO_NAME);
        }

        productRepository.save(Product.from(request));
    }

    public void deleteById(Long productId) {
        productRepository.findOrThrow(productId);

        productRepository.deleteById(productId);
    }

    // Pageable 객체 유효성 검사
    public void validate(Pageable pageable) {
        Sort sort = pageable.getSort();

        for (Sort.Order order : sort) {
            if (!ALLOWED_SORT_NAMES.contains(order.getProperty())) {
                throw InvalidRequestException.from(ErrorCode.INVALID_SORT_NAMES);
            }

        }
    }
}
