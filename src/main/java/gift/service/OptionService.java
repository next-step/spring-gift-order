package gift.service;

import gift.entity.Option;
import gift.exception.NotFoundException;
import gift.repository.OptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OptionService {
    private final OptionRepository optionRepository;

    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public Option findOptionById(Long id) {
        return optionRepository.findById(id).orElseThrow(() -> new NotFoundException("옵션", id));
    }

    public Page<Option> findAllOptions(Pageable pageable) {
        return optionRepository.findAll(pageable);
    }

    public Option addOption(String name) {
        return optionRepository.save(new Option(name));
    }

    public Option updateOption(Long id, String name) {
        Option option = findOptionById(id);
        option.update(name);
        return option;
    }

    public void deleteOption(Long id) {
        optionRepository.deleteById(id);
    }
}
