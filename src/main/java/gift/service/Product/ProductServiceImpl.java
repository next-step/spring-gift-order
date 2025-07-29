package gift.service.Product;

import gift.common.code.CustomResponseCode;
import gift.common.exception.core.CustomException;
import gift.common.util.SortUtil;
import gift.dto.Pagination.PageResponse;
import gift.dto.Pagination.Pagination;
import gift.dto.Product.Option.ProductOptionRequest;
import gift.dto.Product.ProductRequest;
import gift.dto.Product.ProductResponse;
import gift.dto.Product.ProductSortField;
import gift.dto.Product.ProductUpdateRequest;
import gift.entity.Product.Product;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product(request.name(), request.price(), request.imageUrl());

        for (ProductOptionRequest optionRequest : request.options()) {
            product.addUniqueOption(optionRequest.name(), optionRequest.quantity());
        }

        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(Pagination pagination) {
        Sort sortCondition = SortUtil.createSort(
            pagination.getSort(),
            ProductSortField.allowedFields()
        );

        Pageable pageable = PageRequest.of(pagination.getPage() - 1,
            pagination.getSize(),
            sortCondition
        );

        Page<ProductResponse> page = productRepository
            .findAll(pageable)
            .map(ProductResponse::from);

        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(CustomResponseCode.NOT_FOUND));

        return ProductResponse.from(product);
    }

    @Override
    @Transactional
    public ProductResponse update(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(CustomResponseCode.NOT_FOUND));

        product.update(request.name(), request.price(), request.imageUrl());
        productRepository.save(product);

        return ProductResponse.from(product);
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new CustomException(CustomResponseCode.NOT_FOUND);
        }

        productRepository.deleteById(productId);
    }
}
