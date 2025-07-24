package gift.service;

import gift.domain.Product;
import gift.domain.ProductOption;
import gift.repository.ProductOptionRepository;
import gift.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;

    public ProductOptionService(ProductRepository productRepository, ProductOptionRepository optionRepository) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
    }

    @Transactional
    public void addOptionToProduct(Long productId, String optionName, Long quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        ProductOption option = new ProductOption(optionName, quantity);
        product.addOption(option);
        product.validateAtLeastOneOption();

        productRepository.save(product);
    }

    @Transactional
    public void subtractQuantity(Long optionId, long quantity) {
        ProductOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션이 존재하지 않습니다."));
        option.subtractQuantity(quantity);
    }

    public List<ProductOption> getOptionsByProductId(Long productId) {
        return optionRepository.findByProductId(productId);
    }
}
