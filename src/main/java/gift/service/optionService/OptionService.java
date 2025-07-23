package gift.service.optionService;

import gift.dto.optionDto.OptionRequestDto;
import gift.entity.ItemOption;

import java.util.List;

public interface OptionService {
    ItemOption save(OptionRequestDto optionRequestDto, Long itemId);

    List<ItemOption> getOptions(Long itemId);

    ItemOption quantityControl(OptionRequestDto optionRequestDto, Long itemId);
}
