package gift.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ProductNameValidator implements ConstraintValidator<ValidProductName, String> {

    private boolean allowKakao;

    private static final Pattern ALLOWED_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣 ()\\[\\]+\\-\\&/_]*$");

    @Override
    public void initialize(ValidProductName constraintAnnotation) {
        this.allowKakao = constraintAnnotation.allowKakao();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        if (value.contains("카카오")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("상품명에 '카카오'는 사용할 수 없습니다. 담당 MD와 협의 필요.")
                    .addConstraintViolation();
            return false;
        }

        if (!ALLOWED_PATTERN.matcher(value).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("상품명에는 (), [], +, -, &, /, _ 외 특수문자를 사용할 수 없습니다.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
