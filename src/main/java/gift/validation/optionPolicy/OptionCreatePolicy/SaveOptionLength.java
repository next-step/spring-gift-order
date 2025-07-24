package gift.validation.optionPolicy.OptionCreatePolicy;

import gift.dto.optionDto.OptionRequestDto;
import gift.validation.itemPolicy.ItemViolationHandler.ViolationHandler;
import gift.validation.optionPolicy.OptionPolicy;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;


@Component
public class SaveOptionLength implements OptionPolicy<OptionRequestDto> {
    private final ViolationHandler violationHandler;

    public SaveOptionLength(ViolationHandler violationHandler) {
        this.violationHandler = violationHandler;
    }

    @Override
    public boolean isValid(OptionRequestDto dto, ConstraintValidatorContext context) {
        if (dto.optionName().length() > 50) {
            violationHandler.addViolation(context, "상품 이름은 최대 50자까지 입니다.");
            return false;
        }
        return true;
    }
}
