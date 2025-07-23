package gift.validation.optionPolicy;

import jakarta.validation.ConstraintValidatorContext;

public interface OptionPolicy<T> {
    boolean isValid(T dto, ConstraintValidatorContext context);
}