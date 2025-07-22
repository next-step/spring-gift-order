package gift.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductNameValidator implements ConstraintValidator<ProductName, String> {

    private static final String FORBIDDEN_WORD = "카카오";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && value.contains(FORBIDDEN_WORD)) {
            return false;
        }
        return true;
    }
}