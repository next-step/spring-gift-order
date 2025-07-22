package gift.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class RestrictedWordsValidator implements
    ConstraintValidator<RestrictedWords, String> {

    private List<String> restrictedWords;

    @Override
    public void initialize(RestrictedWords constraintAnnotation) {
        restrictedWords = Arrays.asList(constraintAnnotation.words());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        for (String word : restrictedWords) {
            if (!word.isEmpty() && value.contains(word)) {
                return false;
            }
        }
        return true;
    }
}
