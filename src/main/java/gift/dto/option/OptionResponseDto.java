package gift.dto.option;

import gift.entity.Option;

public record OptionResponseDto(
        Long id,
        String name,
        int quantity
) {
    public static OptionResponseDto from(Option option) {
        return new OptionResponseDto(option.getId(), option.getName(), option.getQuantity());
    }
}