package gift.service;

import gift.dto.CreateOptionRequest;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class OptionService {

    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;

    public OptionService(ProductRepository productRepository, OptionRepository optionRepository) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
    }

    public Option create(Long productId, CreateOptionRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        if(optionRepository.existsByNameAndProduct(request.name(), product)) {
            throw new IllegalArgumentException("이미 추가된 옵션입니다.");
        }

        Option option = Option.of(request.name(), request.quantity(), product);

        Option saved = optionRepository.save(option);

        return saved;
    }

    public List<Option> getAllOption(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        return optionRepository.findAllByProduct(product);
    }

    public void subtractQuantity(Long optionId, int quantity) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(()-> new NoSuchElementException("옵션이 존재하지 않습니다."));
        option.subtract(quantity);
        optionRepository.save(option);
    }
}
