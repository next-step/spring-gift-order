package gift.service;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.entity.Option;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.exception.InvalidProductNameException;
import gift.repository.OptionRepository;
import gift.repository.ProductOptionRepository;
import gift.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import gift.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {
    
    // 의존성 고정
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final ProductOptionRepository productOptionRepository;
    private final List<String> forbiddenWords; // MD 협의 단어 목록

    // 의존성 주입
    public ProductService(ProductRepository productRepository,
                          @Value("${forbidden_words}") String forbiddenWordsProp,
                          OptionRepository optionRepository,
                          ProductOptionRepository productOptionRepository) {
        this.productRepository = productRepository;
        this.forbiddenWords = Arrays.stream(forbiddenWordsProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        this.optionRepository = optionRepository;
        this.productOptionRepository = productOptionRepository;
    }

    @Transactional
    public ProductResponseDto findProductById(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product", id));
        return new ProductResponseDto(product);
    }

    @Transactional
    public Page<ProductResponseDto> findAllProduct(Pageable pageable){
        return productRepository.findAll(pageable).map(ProductResponseDto::new);
    }

    @Transactional
    public ProductResponseDto saveProduct(ProductRequestDto requestDto){
        List<String> matched = forbiddenWords.stream().filter(requestDto.name()::contains).toList();
        if(!matched.isEmpty()){ // 금지 단어 포함돼있을 경우 예외 던지기
            throw new InvalidProductNameException(matched);
        }
        return new ProductResponseDto(productRepository.save(new Product(requestDto)));
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto){
        List<String> matched = forbiddenWords.stream().filter(requestDto.name()::contains).toList();
        if(!matched.isEmpty()){ // 금지 단어 포함돼있을 경우 예외 던지기
            throw new InvalidProductNameException(matched);
        }
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product", id));
        product.update(requestDto.name(), requestDto.price(), requestDto.imageUrl());
        return new ProductResponseDto(product);
    }

    @Transactional
    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }


    //////////////////////////////// 상품 옵션 ///////////////////////////////////


    @Transactional
    public Page<ProductOption> findProductOptionByProductId(Long productId, Pageable pageable) {
        return productOptionRepository.findByProductId(productId, pageable);
    }
    @Transactional
    public void addProductOption(Long productId, String optionName, Long value) {
        Option option;
        if(optionRepository.existsByName(optionName)) {
            option = optionRepository.findByName(optionName).orElse(null);
        }
        else {
            option = optionRepository.save(new Option(optionName));
        }

        Product product = productRepository.findById(productId).orElseThrow(NotFoundException::new);

        productOptionRepository.save(new ProductOption(product, option, value));
    }

    @Transactional
    public void subtractProductOption(Long productOptionId, Long value) {
        ProductOption productOption = productOptionRepository.findById(productOptionId).orElseThrow(NotFoundException::new);
        productOption.subtract(value);
    }

    @Transactional
    public void deleteProductOption(Long productOptionId) {
        productOptionRepository.deleteById(productOptionId);
    }
}
