package gift.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = OnlyPermittedSymbolsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyPermittedSymbols {

    String message() default "허용되지 않은 특수문자가 포함되어 있습니다. (허용: 한글, 영문, 숫자, 공백, (, ), [, ], +, -, &, /, _)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
