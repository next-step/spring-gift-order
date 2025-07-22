package gift.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OptionTest {

    @Test
    @DisplayName("재고를 정상적으로 차감한다")
    void subtract_Success() {
        // given
        Option option = new Option("테스트 옵션", 100);

        // when
        option.subtract(10);

        // then
        assertThat(option.getQuantity()).isEqualTo(90);
    }

    @Test
    @DisplayName("재고보다 많은 수량을 차감하면 예외가 발생한다")
    void subtract_Fail_WhenQuantityIsInsufficient() {
        // given
        Option option = new Option("테스트 옵션", 10);

        // when & then
        assertThatThrownBy(() -> option.subtract(11))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");
    }
}
