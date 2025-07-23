package gift.validation.optionPolicy.OptionViolationHandler;

import jakarta.validation.ConstraintValidatorContext;

public class OptionNameViolationHandler implements ViolationHandler {
    @Override
    public void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addPropertyNode("optionName").addConstraintViolation();
    }
}
