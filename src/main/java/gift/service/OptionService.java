package gift.service;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.OptionRequest;
import gift.dto.OptionResponse;
import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.repository.OptionJpaRepository;
import gift.repository.ProductJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OptionService {

    private final OptionJpaRepository optionJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    public OptionService(OptionJpaRepository optionJpaRepository, ProductJpaRepository productJpaRepository) {
        this.optionJpaRepository = optionJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    public List<OptionResponse> getOptionsByProductId(Long productId) {
        validateProductExists(productId);
        
        List<Option> options = optionJpaRepository.findByProductId(productId);
        return options.stream()
                .map(OptionResponse::from)
                .toList();
    }

    @Transactional
    public OptionResponse addOption(Long productId, OptionRequest request) {
        Product product = findProductById(productId);
        
        validateDuplicateOptionName(productId, request.name());
        
        Option option = Option.of(request.name(), request.quantity(), product);
        Option savedOption = optionJpaRepository.save(option);
        
        return OptionResponse.from(savedOption);
    }

    @Transactional
    public void subtractOptionQuantity(Long optionId, Integer quantity) {
        Option option = findOptionById(optionId);
        option.subtract(quantity);
        optionJpaRepository.save(option);
    }

    @Transactional
    public OptionResponse updateOption(Long optionId, OptionRequest request) {
        Option option = findOptionById(optionId);
        
        // 이름이 변경되는 경우 중복 검사
        if (!option.name().equals(request.name())) {
            validateDuplicateOptionName(option.product().id(), request.name());
        }
        
        option.updateName(request.name());
        option.updateQuantity(request.quantity());
        
        Option savedOption = optionJpaRepository.save(option);
        return OptionResponse.from(savedOption);
    }

    @Transactional
    public void deleteOption(Long optionId) {
        Option option = findOptionById(optionId);
        optionJpaRepository.delete(option);
    }

    private void validateProductExists(Long productId) {
        if (!productJpaRepository.existsById(productId)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    private Product findProductById(Long productId) {
        return productJpaRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private Option findOptionById(Long optionId) {
        return optionJpaRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
    }

    private void validateDuplicateOptionName(Long productId, String optionName) {
        if (optionJpaRepository.existsByProductIdAndName(productId, optionName)) {
            throw new BusinessException(ErrorCode.OPTION_NAME_DUPLICATE);
        }
    }
}
