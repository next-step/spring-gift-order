package gift.validation.optionPolicy.OptionViolationHandler;

import jakarta.validation.ConstraintValidatorContext;

public interface ViolationHandler {
    void addViolation(ConstraintValidatorContext context, String message);
}
