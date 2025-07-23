package gift.service;

import gift.dto.ProductOptionRequest;
import gift.dto.ProductOptionResponse;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.repository.ProductOptionRepository;
import gift.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductOptionService {
    private final ProductOptionRepository optionRepository;
    private final ProductRepository productRepository;

    public ProductOptionService(ProductOptionRepository optionRepository,
                                ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductOptionResponse> getOptionsByProductId(Long productId) {
        validateProductExists(productId);

        List<ProductOption> options = optionRepository.findByProductId(productId);
        return options.stream()
                .map(ProductOptionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductOptionResponse createOption(Long productId, ProductOptionRequest request) {
        Product product = findProductById(productId);

        if (optionRepository.existsByProductIdAndName(productId, request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 옵션 이름입니다: " + request.getName());
        }

        ProductOption option = new ProductOption(request.getName(), request.getQuantity());
        product.addOption(option);

        ProductOption savedOption = optionRepository.save(option);
        return ProductOptionResponse.from(savedOption);
    }

    @Transactional
    public ProductOption subtractOptionQuantity(Long productId, Long optionId, int quantity) {
        ProductOption option = findValidatedOption(productId, optionId);

        option.subtract(quantity);
        return optionRepository.save(option);
    }

    @Transactional
    public ProductOption addOptionQuantity(Long productId, Long optionId, int quantity) {
        ProductOption option = findValidatedOption(productId, optionId);

        option.add(quantity);
        return optionRepository.save(option);
    }

    private ProductOption findValidatedOption(Long productId, Long optionId) {
        validateProductExists(productId);
        ProductOption option = optionRepository.findByIdAndProductId(optionId, productId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다: " + optionId));

        return option;
    }

    private void validateProductExists(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId);
        }
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));
    }
}
