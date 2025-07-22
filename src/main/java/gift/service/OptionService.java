package gift.service;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.OptionResponse;
import gift.dto.PageResponse;
import gift.repository.OptionJpaRepository;
import gift.repository.ProductJpaRepository;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptionService {

    private final OptionJpaRepository optionRepository;
    private final ProductJpaRepository productRepository;

    public OptionService(OptionJpaRepository optionRepository, ProductJpaRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }


    @Transactional
    public Option addOption(String name, int quantity, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));

        Option option = new Option(null, name, quantity, product);

        if (optionRepository.findByProductId(productId).stream()
                .anyMatch(o -> o.getName().equals(name))) {
            throw new IllegalArgumentException("중복된 option은 추가될 수 없습니다.");
        }
        return optionRepository.save(option);
    }

    public PageResponse<OptionResponse> findOptionsByProductId(Long productId, Pageable pageable) {
        return PageResponse.from(optionRepository.findByProductId(productId, pageable)
                .map(option -> new OptionResponse(
                        option.getId(),
                        option.getName(),
                        option.getQuantity())));
    }

    @Transactional
    public void subtractQuantity(Long optionId, int quantity) {
        Option findOption = optionRepository.findById(optionId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 옵션입니다."));

        findOption.subtractQuantity(quantity);
    }

}
