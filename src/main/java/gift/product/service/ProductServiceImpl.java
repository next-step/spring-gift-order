package gift.product.service;

import gift.product.Product;
import gift.product.ProductOption;
import gift.product.dto.ProductAddRequestDto;
import gift.product.dto.ProductOptionAddRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.dto.ProductUpdateRequestDto;
import gift.product.exception.InvalidProductOptionException;
import gift.product.exception.ProductNotFoundException;
import gift.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void addProduct(ProductAddRequestDto requestDto) {
        // 옵션 생성
        List<ProductOption> options = createValidProductOptions(requestDto.options());

        // 상품 생성 후 옵션 할당
        Product product = new Product(requestDto.name(), requestDto.price(), requestDto.url());
        product.setOptions(options);

        productRepository.save(product);
    }

    @Override
    public ProductResponseDto findProductById(Long id) {
        Product product = findProductByIdOrElseThrow(id);
        return new ProductResponseDto(product);
    }

    @Override
    public List<ProductResponseDto> findAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(Product::toProductResponseDto).toList();
    }

    @Override
    public Page<ProductResponseDto> findAllProductsWithPageable(Pageable pageable) {
        return productRepository.findAll(pageable).map(Product::toProductResponseDto);
    }

    @Override
    @Transactional
    public void updateProductById(Long id, ProductUpdateRequestDto requestDto) {
        Product product = findProductByIdOrElseThrow(id);

        List<ProductOption> options = createValidProductOptions(requestDto.options());

        product.update(requestDto);
        product.setOptions(options);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product findProductByIdOrElseThrow(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    private List<ProductOption> createValidProductOptions(List<ProductOptionAddRequestDto> optionDto) {
        if (optionDto == null || optionDto.isEmpty()) {
            throw new InvalidProductOptionException("optionError","상품에는 최소 하나 이상의 옵션이 있어야 합니다.");
        }

        return optionDto.stream()
                .filter(Objects::nonNull)
                .map(opt -> new ProductOption(opt.name(), opt.quantity()))
                .toList();
    }
}
