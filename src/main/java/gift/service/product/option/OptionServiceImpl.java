package gift.service.product.option;

import gift.common.exception.NotFoundException;
import gift.dto.product.option.ProductOptionRequest;
import gift.dto.product.option.ProductOptionResponse;
import gift.entity.product.Product;
import gift.entity.product.option.ProductOption;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptionServiceImpl implements OptionService {

    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;

    public OptionServiceImpl(ProductRepository productRepository,
        OptionRepository optionRepository) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
    }

    @Override
    @Transactional
    public ProductOptionResponse add(Long productId, ProductOptionRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(NotFoundException::new);

        ProductOption option = product.addUniqueOption(request.name(), request.quantity());
        ProductOption savedOption = optionRepository.save(option);

        return ProductOptionResponse.from(savedOption);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOptionResponse> getOptionsByProductId(Long productId) {
        return optionRepository.findByProductId(productId).stream()
            .map(ProductOptionResponse::from)
            .toList();
    }

    @Override
    @Transactional
    public ProductOptionResponse update(Long optionId, ProductOptionRequest request) {
        ProductOption option = optionRepository.findById(optionId)
            .orElseThrow(NotFoundException::new);

        option.update(request.name(), request.quantity());

        return ProductOptionResponse.from(option);
    }
}
