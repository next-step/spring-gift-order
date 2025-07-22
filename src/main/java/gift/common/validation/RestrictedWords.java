package gift.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = RestrictedWordsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestrictedWords {

    String message() default "금지된 단어가 포함되어 있습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] words();
}
