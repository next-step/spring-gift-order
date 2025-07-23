package gift.service;

import gift.dto.ProductOptionRequest;
import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.exception.product.MdApprovalException;
import gift.exception.product.MdApprovalMissingException;
import gift.exception.product.ProductNotFoundException;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findAllProducts() {
        List<Product> products = productRepository.findAllByOrderByIdAsc();
        return products.stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(ProductResponseDto::new);
    }

    public void validateMdApprovalForSave(ProductRequestDto dto) {
        if (dto.name().contains("카카오")) {
            throw new MdApprovalException("상품 이름에 '카카오'가 포함된 상품은 MD 승인 후 등록할 수 있습니다.");
        }
    }

    @Override
    @Transactional
    public ProductResponseDto saveProduct(ProductRequestDto dto) {
        validateMdApprovalForSave(dto);
        if (dto.options() == null || dto.options().isEmpty()) {
            throw new IllegalArgumentException("상품에는 하나 이상의 옵션이 있어야 합니다.");
        }

        Product product = new Product(dto.name(), dto.price(), dto.imageUrl());

        for (ProductOptionRequest optionRequest : dto.options()) {
            ProductOption option = new ProductOption(optionRequest.getName(), optionRequest.getQuantity());
            product.addOption(option);
        }
        product.validateOptions();

        Product savedProduct = productRepository.save(product);
        return new ProductResponseDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return new ProductResponseDto(product);
    }

    public void validateMdApprovalForUpdate(ProductRequestDto dto, Boolean mdApproved) {
        if (mdApproved == null) {
            throw new MdApprovalMissingException("MD 승인 상태를 확인할 수 없습니다.");
        }
        if (dto.name().contains("카카오") && !mdApproved) {
            throw new MdApprovalException("상품 이름에 '카카오'가 포함된 상품은 MD 승인 후 등록할 수 있습니다.");
        }
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        Boolean mdApproved = product.isApproved();
        validateMdApprovalForUpdate(dto, mdApproved);

        product.update(dto.name(), dto.price(), dto.imageUrl());
        return new ProductResponseDto(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }
}