package gift.validation.optionPolicy.OptionCreatePolicy;

import gift.dto.optionDto.OptionRequestDto;
import gift.validation.itemPolicy.ItemViolationHandler.ViolationHandler;
import gift.validation.optionPolicy.OptionPolicy;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


@Component
public class SaveOptionSpecialSymbol implements OptionPolicy<OptionRequestDto> {

    private static final Pattern pattern = Pattern.compile("^[a-zA-Z0-9()\\[\\]+\\-\\&/_가-힣ㄱ-ㅎㅏ-ㅣ\\s]*$");
    private final ViolationHandler violationHandler;

    public SaveOptionSpecialSymbol(ViolationHandler violationHandler) {
        this.violationHandler = violationHandler;
    }

    @Override
    public boolean isValid(OptionRequestDto optionRequestDto, ConstraintValidatorContext context) {
        if (!pattern.matcher(optionRequestDto.optionName()).matches()) {
            violationHandler.addViolation(context, "( ), [ ], +, -, &, /, _\" 외에는 특수 문자가 허용되지 않습니다.");
            return false;
        }
        return true;
    }
}
