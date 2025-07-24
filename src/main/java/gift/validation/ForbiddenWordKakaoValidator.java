package gift.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ForbiddenWordKakaoValidator implements ConstraintValidator<ForbiddenWordKakao, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return !value.contains("카카오");
    }
}