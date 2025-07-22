package gift.service;

import gift.common.code.CustomResponseCode;
import gift.common.exception.CustomException;
import gift.common.util.SortUtil;
import gift.dto.PageResponse;
import gift.dto.Pagination;
import gift.dto.ProductOptionRequest;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.dto.ProductSortField;
import gift.entity.Product;
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
        if (request.options() == null || request.options().isEmpty()) {
            throw new CustomException(CustomResponseCode.OPTION_REQUIRED);
        }

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
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new CustomException(CustomResponseCode.NOT_FOUND));

        return ProductResponse.from(product);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        boolean updated = productRepository.updateProduct(id, request.name(),
            request.price(), request.imageUrl()) > 0;

        if (!updated) {
            throw new CustomException(CustomResponseCode.NOT_FOUND);
        }

        Product updatedProduct = productRepository.findById(id)
            .orElseThrow(() -> new CustomException(CustomResponseCode.NOT_FOUND));

        return ProductResponse.from(updatedProduct);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new CustomException(CustomResponseCode.NOT_FOUND);
        }

        productRepository.deleteById(id);
    }
}
