package gift.service;

import gift.dto.OptionResponse;
import gift.repository.OptionRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptionService {

    private final OptionRepository optionRepository;

    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @Transactional(readOnly = true)
    public List<OptionResponse> getOptionsByProductId(Long productId) {
        return optionRepository.findByProductId(productId).stream()
            .map(OptionResponse::from)
            .collect(Collectors.toList());
    }
}
