package gift.service;

import gift.dto.OptionRequest;
import gift.dto.OptionResponse;
import gift.entity.Option;
import gift.entity.Product;
import gift.entity.vo.OptionName;
import gift.entity.vo.OptionQuantity;
import gift.enums.OptionSortKey;
import gift.exception.DuplicateOptionException;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    public List<OptionResponse> getOptionPage(Long productId, int page, int size, OptionSortKey sortKey) {
        PageRequest pageRequest = PageRequest.of(page, size, sortKey.getSort());

        return optionRepository.findAllByProductId(productId, pageRequest)
                .stream()
                .map(OptionResponse::of)
                .toList();
    }

    public OptionResponse createOption(Long productId, OptionRequest optionRequest) {
        OptionName name = new OptionName(optionRequest.getName());
        OptionQuantity quantity = new OptionQuantity(optionRequest.getQuantity());

        if (optionRepository.existsByProductIdAndName(productId, name)) {
            throw new DuplicateOptionException("이미 동일한 옵션이 존재합니다.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다. " +
                        "productId = " + productId));

        Option option = new Option(product, name, quantity);

        return OptionResponse.of(optionRepository.save(option));
    }

    public OptionResponse subtractOptionQuantity(Long optionId, int quantity) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 옵션이 존재하지 않습니다."));

        option.subtract(quantity);

        return OptionResponse.of(option);
    }
}
