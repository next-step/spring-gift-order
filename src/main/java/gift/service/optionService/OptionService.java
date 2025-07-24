package gift.service.optionService;

import gift.dto.optionDto.OptionRequestDto;
import gift.entity.ItemOption;

import java.util.List;

public interface OptionService {
    ItemOption save(ItemOption itemOption, Long itemId);

    List<ItemOption> getOptions(Long itemId);

    ItemOption quantityControl(ItemOption targetOption, Long itemId);
}
