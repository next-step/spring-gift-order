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

    private final ProductOptionRepository optionRepository;
    private final ProductRepository productRepository;

    public ProductOptionService(ProductOptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    public List<ProductOption> getOptionsByProductId(Long productId) {
        return optionRepository.findByProductId(productId);
    }

    @Transactional
    public void addOptionToProduct(Long productId, String name, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다: id=" + productId));

        if (name == null || name.length() > 50 || !name.matches("^[\\p{L}\\d\\s\\(\\)\\[\\]\\+\\-&/_\\.]+$")) {
            throw new IllegalArgumentException("옵션 이름이 형식에 맞지 않습니다.");
        }


        if (quantity < 1 || quantity >= 100_000_000) {
            throw new IllegalArgumentException("옵션 수량은 1 이상 1억 미만이어야 합니다.");
        }

        boolean exists = optionRepository.existsByProductIdAndName(productId, name);
        if (exists) {
            throw new IllegalStateException("같은 상품에 동일한 옵션 이름이 이미 존재합니다.");
        }

        ProductOption option = new ProductOption(name, quantity, product);
        optionRepository.save(option);
    }
}
