package gift.service;

import gift.Entity.Option;
import gift.repository.OptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionService {

    private final OptionRepository optionRepository;

    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    // 특정 상품의 옵션 목록 조회
    public List<Option> findOptionsByProductId(Long productId) {
        return optionRepository.findByProductId(productId);
    }

    // 옵션 중복 여부 확인
    public boolean isDuplicateOption(Long productId, String optionName) {
        return optionRepository.existsByProductIdAndName(productId, optionName);
    }
}

