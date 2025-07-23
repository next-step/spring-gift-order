package gift;

import gift.entity.ProductOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class ProductOptionTest {

    @Test
    @DisplayName("올바른 옵션 이름과 수량으로 옵션 생성")
    void createOptionWithValidNameAndQuantity() {
        String name = "01 [Best] 시어버터 핸드 & 시어 스틱 립 밤";
        int quantity = 100;

        ProductOption option = new ProductOption(name, quantity);

        assertThat(option.getName()).isEqualTo(name);
        assertThat(option.getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("옵션 이름이 null이면 예외 발생")
    void createOptionWithNullName() {
        String name = null;
        int quantity = 100;

        assertThatThrownBy(() -> new ProductOption(name, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션 이름은 필수입니다.");
    }

    @Test
    @DisplayName("옵션 이름이 50자를 초과하면 예외 발생")
    void createOptionWithNameTooLong() {
        String name = "a".repeat(51);
        int quantity = 100;

        assertThatThrownBy(() -> new ProductOption(name, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션 이름은 50자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("허용되지 않는 특수문자가 포함된 옵션 이름으로 예외 발생")
    void createOptionWithInvalidSpecialCharacters() {
        String name = "옵션명@#$%";
        int quantity = 100;

        assertThatThrownBy(() -> new ProductOption(name, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("옵션 이름에 허용되지 않는 특수문자가 포함되어 있습니다: [@#$%]");
    }

    @Test
    @DisplayName("허용되는 특수문자가 포함된 옵션 이름으로 성공적으로 생성")
    void createOptionWithValidSpecialCharacters() {
        String name = "옵션명() [] + - & / _";
        int quantity = 100;

        ProductOption option = new ProductOption(name, quantity);

        assertThat(option.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("옵션 수량이 1 미만이면 예외 발생")
    void createOptionWithQuantityLessThanOne() {
        String name = "테스트 옵션";
        int quantity = 0;

        assertThatThrownBy(() -> new ProductOption(name, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션 수량은 최소 1개 이상이어야 합니다.");
    }

    @Test
    @DisplayName("옵션 수량이 1억 이상이면 예외 발생")
    void createOptionWithQuantityTooLarge() {
        String name = "테스트 옵션";
        int quantity = 100_000_000;

        assertThatThrownBy(() -> new ProductOption(name, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션 수량은 100000000개 미만이어야 합니다.");
    }

    @Test
    @DisplayName("정상적인 수량 차감")
    void subtractQuantityNormally() {
        ProductOption option = new ProductOption("테스트 옵션", 100);

        option.subtract(30);

        assertThat(option.getQuantity()).isEqualTo(70);
    }

    @Test
    @DisplayName("재고보다 많은 수량을 차감하면 예외 발생")
    void subtractQuantityMoreThanStock() {
        ProductOption option = new ProductOption("테스트 옵션", 50);

        assertThatThrownBy(() -> option.subtract(100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다. 현재 재고: 50, 요청 수량: 100");
    }

    @Test
    @DisplayName("0 이하의 수량을 차감하면 예외 발생")
    void subtractZeroOrNegativeQuantity() {
        ProductOption option = new ProductOption("테스트 옵션", 100);

        assertThatThrownBy(() -> option.subtract(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차감할 수량은 1 이상이어야 합니다.");

        assertThatThrownBy(() -> option.subtract(-10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차감할 수량은 1 이상이어야 합니다.");
    }
}
