package gift.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OptionTest {

    private Option option;

    @BeforeEach
    void setUp() {
        Product product = new Product("Test Product", 10000, "test.jpg");
        option = new Option("Test Option", 100, product);
    }

    @Test
    void subtract() {
        option.subtract(10);
        assertThat(option.getQuantity()).isEqualTo(90);
    }

    @Test
    void subtract_NotEnough() {
        assertThatThrownBy(() -> option.subtract(110))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 부족합니다.");
    }
}
