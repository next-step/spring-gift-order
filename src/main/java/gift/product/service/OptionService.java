package gift.product.service;

import gift.product.dto.request.OptionCreateRequest;
import gift.product.dto.response.OptionResponse;
import gift.product.entity.Option;
import gift.product.entity.Product;
import gift.product.repository.OptionRepository;
import gift.product.repository.ProductRepository;
import gift.product.status.ProductStatus;
import gift.shared.exception.product.NoProductException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionService {
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;

    public OptionService(ProductRepository productRepository, OptionRepository optionRepository) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
    }

    public List<OptionResponse> getAllOptionsByProductId(Long productId) {
        return optionRepository.findAllByProductId(productId).stream()
                .map(OptionResponse::from)
                .toList();
    }

    public OptionResponse save(Long productId, OptionCreateRequest optionCreateRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoProductException(ProductStatus.NO_GIFT.getMessage()));
        Option option = new Option(optionCreateRequest, product);
        product.addOption(option);
        return OptionResponse.from(optionRepository.save(option));
    }
}
