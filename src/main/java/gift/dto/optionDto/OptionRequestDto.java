package gift.dto.optionDto;

import gift.validation.optionPolicy.optionFieldValid;

@optionFieldValid
public record OptionRequestDto(String optionName, Integer quantity) {
}
