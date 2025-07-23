package gift.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ProductNameValidator implements ConstraintValidator<ProductNameValid, String> {

    private static final String pattern = "^[a-zA-Z0-9가-힣ㄱ-ㆎ\\s()\\[\\]+&/_-]+$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.length() > 15) {
            addConstraintViolation(context, "상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있습니다.");
            return false;
        }

        if (!Pattern.matches(pattern, value)) {
            addConstraintViolation(context, "특수 문자 ( ), [ ], +, -, &, /, _ 외는 사용 불가합니다.");
            return false;
        }

        if (value.contains("카카오")) {
            addConstraintViolation(context, "\"카카오\"가 포함된 문구는 담당 MD와 협의한 경우에만 사용할 수 있습니다.");
            return false;
        }

        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
