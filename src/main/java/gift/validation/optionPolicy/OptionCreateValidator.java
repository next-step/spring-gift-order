package gift.validation.optionPolicy;

import gift.dto.optionDto.OptionRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class OptionCreateValidator implements ConstraintValidator<optionFieldValid, OptionRequestDto> {

    private final List<OptionPolicy<OptionRequestDto>> policies;

    public OptionCreateValidator(List<OptionPolicy<OptionRequestDto>> policies) {
        this.policies = policies;
    }

    @Override
    public boolean isValid(OptionRequestDto optionRequestDto, ConstraintValidatorContext context) {
        if (optionRequestDto == null) {
            return false;
        }

        for (OptionPolicy<OptionRequestDto> policy : policies) {
            if (!policy.isValid(optionRequestDto, context)) {
                return false;
            }
        }
        return true;
    }
}
