package gift.service.option;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.option.OptionRequest;
import gift.dto.option.OptionSubtractRequest;
import gift.dto.option.OptionResponse;
import gift.global.exception.AlreadyExistsException;
import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import gift.repository.option.OptionJpaRepository;
import gift.repository.product.ProductJpaRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OptionService {
    private final OptionJpaRepository optionRepository;
    private final ProductJpaRepository productRepository;

    public OptionService(OptionJpaRepository optionRepository,
        ProductJpaRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    // 조회 메서드
    public OptionResponse getOptionById(Long optionId){
        Option option = optionRepository.findOrThrow(optionId);

        return OptionResponse.from(option);
    }

    // 생성 메서드
    public OptionResponse insertOption(OptionRequest optionRequest){
        Product product = productRepository.findOrThrow(optionRequest.productId());

        if(optionRepository.existsByProductIdAndName(optionRequest.productId(), optionRequest.name())){
            throw AlreadyExistsException.from(ErrorCode.ALREADY_EXISTS_NAME);
        }

        Option option = Option.of(
            optionRequest.name(),
            optionRequest.quantity(),
            product
        );

        return OptionResponse.from(optionRepository.save(option));
    }

    // subtract 메서드
    public OptionResponse subtractOption(Long optionId, OptionSubtractRequest optionSubtractRequest){
        Option option = optionRepository.findOrThrow(optionId);
        option.subtractQuantity(optionSubtractRequest.quantity());

        return OptionResponse.from(option);
    }
}
