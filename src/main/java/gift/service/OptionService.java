package gift.service;

import gift.dto.option.OptionRequestDto;
import gift.dto.option.OptionResponseDto;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OptionResponseDto addOptionToProduct(Long productId, OptionRequestDto optionDto) {
        // 1. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 2. 중복 옵션 이름 검사
        if (optionRepository.existsByProductIdAndName(productId, optionDto.name())) {
            throw new IllegalArgumentException("이미 존재하는 옵션 이름입니다.");
        }

        // 3. 옵션 생성 및 상품에 추가
        Option option = new Option(optionDto.name(), optionDto.quantity());
        product.addOption(option);

        // 4. 옵션 저장 및 DTO로 변환하여 반환
        Option savedOption = optionRepository.save(option);
        return OptionResponseDto.from(savedOption);
    }

    @Transactional(readOnly = true)
    public List<OptionResponseDto> getOptionsByProductId(Long productId) {
        return optionRepository.findAllByProductId(productId).stream()
                .map(OptionResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void subtractQuantity(Long optionId, int quantity) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));

        int remainingQuantity = option.getQuantity() - quantity;
        if (remainingQuantity < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        option.setQuantity(remainingQuantity);

    }

    @Transactional
    public void deleteOption(Long optionId) {
        if (!optionRepository.existsById(optionId)) {
            throw new IllegalArgumentException("옵션을 찾을 수 없습니다.");
        }
        optionRepository.deleteById(optionId);
    }
}