package gift.dto.optionDto;

import gift.entity.ItemOption;

public record OptionResponseDto(Long id, String name, Integer quantity) {

    public static OptionResponseDto from(ItemOption itemOption) {
        return new OptionResponseDto(itemOption.getId(), itemOption.getOptionName(), itemOption.getQuantity());
    }
}