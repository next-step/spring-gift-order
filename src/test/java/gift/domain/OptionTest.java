package gift.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OptionTest {

    @Test
    @DisplayName("옵션을 정상적으로 생성할 수 있다")
    void createOption() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        String name = "01. [Best] 시어버터 핸드 & 시어 스틱 립 밤";
        Integer quantity = 969;

        // when
        Option option = Option.of(name, quantity, product);

        // then
        assertThat(option.name()).isEqualTo(name);
        assertThat(option.quantity()).isEqualTo(quantity);
        assertThat(option.product()).isEqualTo(product);
    }

    @Test
    @DisplayName("옵션 이름이 null이면 예외가 발생한다")
    void createOptionWithNullName() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Integer quantity = 100;

        // when & then
        assertThatThrownBy(() -> Option.of(null, quantity, product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션 이름은 비어있을 수 없습니다.");
    }

    @Test
    @DisplayName("옵션 이름이 50자를 초과하면 예외가 발생한다")
    void createOptionWithLongName() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        String longName = "a".repeat(51);
        Integer quantity = 100;

        // when & then
        assertThatThrownBy(() -> Option.of(longName, quantity, product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션 이름은 최대 50자까지 입력할 수 있습니다.");
    }

    @Test
    @DisplayName("옵션 이름에 허용되지 않는 특수문자가 있으면 예외가 발생한다")
    void createOptionWithInvalidSpecialCharacter() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        String invalidName = "테스트@옵션";
        Integer quantity = 100;

        // when & then
        assertThatThrownBy(() -> Option.of(invalidName, quantity, product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션 이름에 허용되지 않는 특수 문자가 포함되어 있습니다. 허용 문자: ( ), [ ], +, -, &, /, _");
    }

    @Test
    @DisplayName("옵션 수량이 1 미만이면 예외가 발생한다")
    void createOptionWithInvalidQuantity() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        String name = "테스트 옵션";
        Integer invalidQuantity = 0;

        // when & then
        assertThatThrownBy(() -> Option.of(name, invalidQuantity, product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션 수량은 1개 이상 100000000개 미만이어야 합니다.");
    }

    @Test
    @DisplayName("옵션 수량을 정상적으로 차감할 수 있다")
    void subtractQuantity() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);

        // when
        option.subtract(30);

        // then
        assertThat(option.quantity()).isEqualTo(70);
    }

    @Test
    @DisplayName("재고보다 많은 수량을 차감하려 하면 예외가 발생한다")
    void subtractMoreThanStock() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 50, product);

        // when & then
        assertThatThrownBy(() -> option.subtract(100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");
    }

    @Test
    @DisplayName("0 이하의 수량을 차감하려 하면 예외가 발생한다")
    void subtractZeroOrNegativeQuantity() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);

        // when & then
        assertThatThrownBy(() -> option.subtract(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차감할 수량은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("허용되는 특수문자로 옵션을 생성할 수 있다")
    void createOptionWithAllowedSpecialCharacters() {
        // given
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        String name = "01. (Best) [시어버터] 핸드+립밤 & 스틱/케어_세트";
        Integer quantity = 100;

        // when
        Option option = Option.of(name, quantity, product);

        // then
        assertThat(option.name()).isEqualTo(name);
    }
}
