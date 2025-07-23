package gift.service;

import gift.dto.request.OptionRequestDto;
import gift.dto.response.OptionResponseDto;
import gift.entity.Option;
import java.util.List;

public interface OptionService {

    Option toOption(Long productId, OptionRequestDto request);

    OptionResponseDto addOption(Long productId, OptionRequestDto optionRequestDto);

    OptionResponseDto updateOption(Long productId, Long optionId,
        OptionRequestDto optionRequestDto);

    void deleteOption(Long productId, Long optionId);

    void subtract(Long optionId, int sub);

    List<OptionResponseDto> getOptions(Long productId);
}
