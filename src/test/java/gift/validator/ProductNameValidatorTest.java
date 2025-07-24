package gift.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductNameValidatorTest {

    private Validator validator;

    static class TestDto {
        @ValidProductName
        String name;

        public TestDto(String name) {
            this.name = name;
        }
    }

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void 허용된_정상_문자_입력_테스트() {
        String name = "상품이름(테스트)+[OK]_";
        Set<ConstraintViolation<TestDto>> violations = validator.validate(new TestDto(name));
        assertThat(violations).isEmpty();
    }

    @Test
    void 허용되지_않은_특수문자_입력_테스트() {
        String name = "상품@이름#";
        Set<ConstraintViolation<TestDto>> violations = validator.validate(new TestDto(name));
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("특수문자");
    }

    @Test
    void 카카오_포함_상품명_테스트() {
        String name = "카카오초콜릿";
        Set<ConstraintViolation<TestDto>> violations = validator.validate(new TestDto(name));
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("카카오");
    }

    @Test
    void 길이_초과_상품명_테스트() {
        String name = "1234567890123456";
        Set<ConstraintViolation<TestDto>> violations = validator.validate(new TestDto(name));
        assertThat(violations).isEmpty();
    }
}

