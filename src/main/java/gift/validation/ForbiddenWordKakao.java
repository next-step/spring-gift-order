package gift.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ForbiddenWordKakaoValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ForbiddenWordKakao {
    String message() default "\"카카오\" 단어는 사용할 수 없습니다.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}