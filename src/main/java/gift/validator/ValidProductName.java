package gift.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProductNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidProductName {

    String message() default "상품명 형식이 올바르지 않습니다.";
    boolean allowKakao() default false;
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
