package gift.service;

import gift.dto.option.OptionRequestDto;
import gift.dto.option.OptionResponseDto;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));
        if (optionRepository.existsByProductIdAndName(productId, optionDto.name())) {
            throw new IllegalArgumentException("상품에 이미 존재하는 옵션 이름입니다.");
        }
        Option option = new Option(optionDto.name(), optionDto.quantity());
        product.addOption(option);
        productRepository.save(product);
        Option savedOption = product.getOptions().get(product.getOptions().size() - 1);
        return new OptionResponseDto(savedOption.getId(), savedOption.getName(), savedOption.getQuantity());
    }

    @Transactional(readOnly = true)
    public List<OptionResponseDto> getOptionsByProductId(Long productId) {
        return optionRepository.findAllByProductId(productId).stream()
                .map(option -> new OptionResponseDto(option.getId(), option.getName(), option.getQuantity()))
                .collect(Collectors.toList());
    }


    @Transactional
    public void updateOption(Long productId, Long optionId, OptionRequestDto optionDto) {
        // 상품 존재 여부 먼저 확인
        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다: " + optionId));

        // 옵션이 해당 상품에 속해 있는지 검증
        if (!option.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("해당 상품에 속한 옵션이 아닙니다.");
        }

        // 이름 중복 검사
        optionRepository.findAllByProductId(productId).stream()
                .filter(o -> !o.getId().equals(optionId) && o.getName().equals(optionDto.name()))
                .findFirst()
                .ifPresent(o -> {
                    throw new IllegalArgumentException("상품에 이미 존재하는 옵션 이름입니다.");
                });

        option.setName(optionDto.name());
        option.setQuantity(optionDto.quantity());
        optionRepository.save(option);
    }


    @Transactional
    public void deleteOption(Long productId, Long optionId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다: " + optionId));

        if (!option.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("해당 상품에 속한 옵션이 아닙니다.");
        }
        optionRepository.delete(option);
    }

    @Transactional
    public void subtractQuantity(Long optionId, int quantity) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 옵션을 찾을 수 없습니다: " + optionId));
        option.subtractQuantity(quantity);
    }
}