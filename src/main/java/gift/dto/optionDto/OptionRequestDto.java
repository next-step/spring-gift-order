package gift.dto.optionDto;

import gift.entity.ItemOption;
import gift.validation.optionPolicy.optionFieldValid;

@optionFieldValid
public record OptionRequestDto(String optionName, Integer quantity) {
    public ItemOption dtoToEntity() {
        return new ItemOption(null, optionName, quantity);
    }
}
