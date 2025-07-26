package gift.dto;

import gift.domain.Option;

public record OptionResponse(
        Long id,
        String name,
        Integer quantity
) {
    public static OptionResponse from(Option option) {
        return new OptionResponse(
                option.id(),
                option.name(),
                option.quantity()
        );
    }
}
