package gift.service;

import gift.dto.request.OptionRequestDto;
import gift.dto.response.OptionResponseDto;
import gift.entity.Option;
import gift.entity.Product;
import gift.exception.OptionNameDuplicationException;
import gift.exception.OptionNotFoundException;
import gift.repository.OptionRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OptionServiceImpl implements OptionService {


    private final OptionRepository optionRepository;
    private final ProductService productService;

    public OptionServiceImpl(OptionRepository optionRepository, ProductService productService) {
        this.optionRepository = optionRepository;
        this.productService = productService;
    }

    @Override
    public Option toOption(Long productId, OptionRequestDto request) {

        Product product = productService.getProduct(productId);
        Option option = new Option();
        option.setName(request.optionName());
        option.setProduct(product);
        option.setQuantity(request.quantity());
        return option;
    }

    @Override
    @Transactional
    public OptionResponseDto addOption(Long productId, OptionRequestDto optionRequestDto) {

        productService.productExist(productId);
        Option option = toOption(productId, optionRequestDto);
        if (optionRepository.existsByProduct_IdAndName(productId, option.getName())) {
            throw new OptionNameDuplicationException("이미 존재하는 옵션명입니다: " + option.getName());
        }
        Option storedOption = optionRepository.save(option);
        return new OptionResponseDto(storedOption);

    }

    @Override
    @Transactional
    public OptionResponseDto updateOption(Long productId, Long optionId,
        OptionRequestDto optionRequestDto) {

        productService.productExist(productId);
        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException("옵션이 존재하지 않습니다"));
        if (optionRepository.existsByProduct_IdAndNameAndIdNot(productId, option.getName(),
            optionId)) {
            throw new OptionNameDuplicationException("이미 존재하는 옵션명입니다: " + option.getName());
        }
        option.setName(optionRequestDto.optionName());
        option.setQuantity(optionRequestDto.quantity());
        Option updatedOption = optionRepository.save(option);
        return new OptionResponseDto(updatedOption);
    }

    @Override
    @Transactional
    public void deleteOption(Long productId, Long optionId) {

        productService.productExist(productId);
        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException("옵션이 존재하지 않습니다"));
        optionRepository.delete(option);
    }

    @Override
    public List<OptionResponseDto> getOptions(Long productId) {

        productService.productExist(productId);
        return optionRepository.findAllByProduct_Id(productId)
            .stream()
            .map(OptionResponseDto::new)
            .toList();
    }

    @Override
    @Transactional
    public void subtract(Long optionId, int sub) {
        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException("옵션이 존재하지 않습니다"));
        option.setQuantity(option.getQuantity() - sub);

        optionRepository.save(option);
    }
}
