package gift.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProductNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductNameValid {
    String message() default "유효하지 않은 이름";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
