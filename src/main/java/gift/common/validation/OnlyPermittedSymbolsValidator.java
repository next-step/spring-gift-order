package gift.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OnlyPermittedSymbolsValidator implements
    ConstraintValidator<OnlyPermittedSymbols, String> {

    private static final String REGEX = "^[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]*$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        
        return value.matches(REGEX);
    }
}
