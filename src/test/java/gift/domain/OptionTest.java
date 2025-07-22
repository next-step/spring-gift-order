package gift.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;

/**
 * 옵션 이름은 공백을 포함하여 최대 50자까지 입력할 수 있다.
 * 특수 문자
 * 가능: ( ), [ ], +, -, &, /, _
 * 그 외 특수 문자 사용 불가
 * 옵션 수량은 최소 1개 이상 1억 개 미만이다.
 */
class OptionTest {

    @Test
    void 옵션_이름은_공백포함_가능() {
        assertThatNoException().isThrownBy(() -> createOption(""));
    }

    @Test
    void 옵션_이름은_50자를_넘을_수_없다() {
        assertThatIllegalArgumentException().isThrownBy(() -> createOption("a".repeat(51)));
    }

    @Test
    void 옵션_이름은_정해진_특수문자만_포함_성공() {
        assertThatNoException().isThrownBy(() -> createOption("name_"));
    }

    @Test
    void 옵션_이름은_정해진_특수문자만_포함_살패() {
        assertThatIllegalArgumentException().isThrownBy(() -> createOption("name*"));
    }

    @Test
    void 옵션_수량이_1억개_이상이면_에러() {
        assertThatIllegalArgumentException().isThrownBy(() -> createOption(100000000));
    }

    @Test
    void 옵션_수량이_0개_미만이면_에러() {
        assertThatIllegalArgumentException().isThrownBy(() -> createOption(-1));
    }


    private static Option createOption(String name) {
        Product product = new Product(null, "name", 1000, "image_url");
        return new Option(null, name, 1, product);
    }
    private static Option createOption(int quantity) {
        Product product = new Product(null, "name", 1000, "image_url");
        return new Option(null, "", quantity, product);
    }

}