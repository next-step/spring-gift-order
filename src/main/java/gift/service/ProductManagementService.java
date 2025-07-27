package gift.service;

import gift.domain.Product;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.dto.common.PageResponse;
import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.repository.ProductJpaRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductManagementService {

    private final ProductJpaRepository productJpaRepository;

    public ProductManagementService(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product newProduct = Product.of(
                request.name(),
                request.validatedPrice(),
                request.imageUrl()
        );
        Product savedProduct = productJpaRepository.save(newProduct);
        return ProductResponse.from(savedProduct);
    }

    public PageResponse<ProductResponse> getAllByPageWithSorting(int page, int size, String sortBy, String sortDirection) {
        validatePageParams(page, size);
        validateSortBy(sortBy);
        
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        org.springframework.data.domain.Page<Product> productPage = productJpaRepository.findAll(pageable);
        
        return createPageResponse(productPage);
    }

    public ProductResponse getById(Long id) {
        Product foundProduct = findProductById(id);
        return ProductResponse.from(foundProduct);
    }

    @Transactional
    public void update(Long id, ProductRequest request) {
        Product existingProduct = findProductById(id);
        updateProductFields(existingProduct, request);
        productJpaRepository.save(existingProduct);
    }

    @Transactional
    public void deleteAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        productJpaRepository.deleteAllById(ids);
    }

    @Transactional
    public void deleteById(Long id) {
        validateProductExists(id);
        productJpaRepository.deleteById(id);
    }

    private void validatePageParams(int page, int size) {
        if (page < 1) {
            throw new IllegalArgumentException("페이지 번호는 1 이상이어야 합니다.");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("페이지 크기는 1 이상 100 이하여야 합니다.");
        }
    }

    private void validateSortBy(String sortBy) {
        List<String> allowedSortFields = List.of("id", "name", "price", "createdAt");
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("정렬 필드는 " + String.join(", ", allowedSortFields) + " 중 하나여야 합니다.");
        }
    }

    private Product findProductById(Long id) {
        return productJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private void validateProductExists(Long id) {
        if (productJpaRepository.findById(id).isEmpty()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page - 1, size, sort);
    }

    private PageResponse<ProductResponse> createPageResponse(org.springframework.data.domain.Page<Product> productPage) {
        List<ProductResponse> responseContent = productPage.getContent().stream()
                .map(ProductResponse::from)
                .toList();

        return new PageResponse<>(
                responseContent,
                productPage.getNumber() + 1,
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.hasNext(),
                productPage.hasPrevious(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }

    private void updateProductFields(Product product, ProductRequest request) {
        product.update(
                request.name(),
                request.validatedPrice(),
                request.imageUrl()
        );
    }
}

