package gift.service;

import gift.common.code.CustomResponseCode;
import gift.common.exception.CustomException;
import gift.dto.ProductOptionRequest;
import gift.dto.ProductOptionResponse;
import gift.entity.Product;
import gift.entity.ProductOption;
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
            .orElseThrow(() -> new CustomException(CustomResponseCode.NOT_FOUND));

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
}
